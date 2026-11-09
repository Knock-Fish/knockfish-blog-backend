package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.config.UploadConfig;
import com.knockfish.convert.GalleryPhotoConvert;
import com.knockfish.dto.gallery.GalleryPhotoQueryDTO;
import com.knockfish.dto.gallery.GalleryPhotoUpdateDTO;
import com.knockfish.entity.GalleryPhoto;
import com.knockfish.exception.CustomException;
import com.knockfish.repository.GalleryAlbumRepository;
import com.knockfish.repository.GalleryPhotoRepository;
import com.knockfish.service.GalleryPhotoService;
import com.knockfish.service.R2FileService;
import com.knockfish.utils.ImageThumbnailUtil;
import com.knockfish.vo.gallery.GalleryBatchUploadVO;
import com.knockfish.vo.gallery.GalleryPhotoVO;
import com.knockfish.vo.gallery.GalleryStatsVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 图库照片服务。
 *
 * <p>上传流程：校验 → 读图取宽高 → 生成缩略图（必备）与预览图（大图才有）→ 上传对象 → 写库。
 * 对象与数据库没有事务，所以对象先传、写库失败回删对象，避免出现「指向空对象的记录」。
 *
 * <p>目录与 key 规则（原图与两类派生图分目录）：
 * <pre>
 * blog/{userId}/{upload.dir.gallery}original/{uuid}.{ext}
 * blog/{userId}/{upload.dir.gallery}thumb/{uuid}.webp
 * blog/{userId}/{upload.dir.gallery}preview/{uuid}.webp
 * </pre>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryPhotoServiceImpl implements GalleryPhotoService {

    /** 允许的图片后缀 */
    private static final List<String> ALLOWED_EXT = Arrays.asList("jpg", "jpeg", "png", "webp", "bmp");
    /** 单张上限，与 multipart 配置对齐 */
    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;
    /** 缩略图宽（列表水瀑流） */
    private static final int THUMB_WIDTH = 640;
    private static final float THUMB_QUALITY = 0.72f;
    /** 预览图长边与质量 */
    private static final int PREVIEW_LONG_EDGE = 1600;
    private static final float PREVIEW_QUALITY = 0.80f;
    /** 触发生成预览图的门槛：长边超 1600 或原图超 800KB */
    private static final int PREVIEW_TRIGGER_EDGE = 1600;
    private static final long PREVIEW_TRIGGER_SIZE = 800L * 1024;
    /** 状态值 */
    private static final int STATUS_NORMAL = 0;
    /** 隐藏：后台仍可见，前台不展示（也查不到详情） */
    private static final int STATUS_HIDDEN = 1;
    private static final int STATUS_RECYCLED = 2;

    /** 批量归册的单次上限，与 GalleryBatchAlbumDTO 的 @Size 保持一致 */
    private static final int BATCH_MOVE_LIMIT = 200;

    /** gallery 下的三个子目录：原图 / 缩略图 / 预览图 */
    private static final String ORIGINAL_DIR = "original/";
    private static final String THUMB_DIR = "thumb/";
    private static final String PREVIEW_DIR = "preview/";

    /** 批量上传最多一次几张（受 max-request-size 100MB / 单张 20MB 约束） */
    private static final int MAX_BATCH_SIZE = 10;
    /** 批量上传的并发度：够快又不至于把 R2 连接打满 */
    private static final int UPLOAD_CONCURRENCY = 4;

    /** 批量上传用固定线程池，随 Bean 生命周期关闭 */
    private final ExecutorService uploadExecutor = Executors.newFixedThreadPool(UPLOAD_CONCURRENCY);

    private final GalleryPhotoRepository galleryPhotoRepository;
    private final GalleryAlbumRepository galleryAlbumRepository;
    private final R2FileService r2FileService;
    private final UploadConfig uploadConfig;
    private final GalleryPhotoConvert galleryPhotoConvert;

    @Override
    public PageInfo<GalleryPhotoVO> getPhotoPage(GalleryPhotoQueryDTO query, Integer pageNum, Integer pageSize) {
        if (query.getStatus() == null) {
            query.setStatus(STATUS_NORMAL);
        }
        try (Page<GalleryPhoto> page = PageHelper.startPage(pageNum, pageSize)) {
            List<GalleryPhoto> list = galleryPhotoRepository.selectPage(query);
            PageInfo<GalleryPhoto> source = PageInfo.of(list);

            PageInfo<GalleryPhotoVO> result = new PageInfo<>();
            result.setPageNum(source.getPageNum());
            result.setPageSize(source.getPageSize());
            result.setTotal(source.getTotal());
            result.setPages(source.getPages());
            result.setList(list.stream().map(this::toVO).toList());
            return result;
        }
    }

    @Override
    public GalleryStatsVO getStats(Long albumId, Boolean onlyUnassigned) {
        GalleryStatsVO stats = galleryPhotoRepository.selectStats(albumId, onlyUnassigned);
        if (stats == null) {
            stats = new GalleryStatsVO();
            stats.setAllCount(0L);
            stats.setLandscape(0L);
            stats.setPortrait(0L);
            stats.setSquare(0L);
            stats.setTotalSize(0L);
            stats.setTrashCount(0L);
        }
        return stats;
    }

    @Override
    public GalleryPhotoVO getPhotoById(Long photoId, boolean onlyVisible) {
        GalleryPhoto photo = galleryPhotoRepository.selectById(photoId);
        if (photo == null || (onlyVisible && !Integer.valueOf(STATUS_NORMAL).equals(photo.getStatus()))) {
            throw new CustomException(404, "图片不存在");
        }
        return toVO(photo);
    }

    @Override
    public GalleryPhotoVO upload(MultipartFile file, String title, Long uploaderId, Long albumId) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(400, "上传的文件不能为空");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new CustomException(400, "单张图片不能超过 20MB");
        }

        String originalName = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
        String ext = extensionOf(originalName);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new CustomException(400, "仅支持 jpg / jpeg / png / webp / bmp 格式");
        }

        BufferedImage source = readImage(file);
        int width = source.getWidth();
        int height = source.getHeight();

        String uuid = UUID.randomUUID().toString().replace("-", "");
        String dir = baseDir(uploaderId);
        // 三类图分目录存放，便于按类型浏览与批量清理
        String originalKey = dir + ORIGINAL_DIR + uuid + "." + ext;
        String thumbKey = dir + THUMB_DIR + uuid + ".webp";
        boolean needPreview = Math.max(width, height) > PREVIEW_TRIGGER_EDGE || file.getSize() > PREVIEW_TRIGGER_SIZE;
        String previewKey = needPreview ? dir + PREVIEW_DIR + uuid + ".webp" : null;

        List<String> uploaded = new ArrayList<>(3);
        try {
            byte[] originalBytes = file.getBytes();
            r2FileService.putObject(originalKey, originalBytes, contentTypeOf(ext));
            uploaded.add(originalKey);

            byte[] thumbBytes = ImageThumbnailUtil.resizeToWebpByWidth(source, THUMB_WIDTH, THUMB_QUALITY);
            r2FileService.putObject(thumbKey, thumbBytes, "image/webp");
            uploaded.add(thumbKey);

            if (needPreview) {
                byte[] previewBytes = ImageThumbnailUtil.resizeToWebpByLongEdge(source, PREVIEW_LONG_EDGE, PREVIEW_QUALITY);
                r2FileService.putObject(previewKey, previewBytes, "image/webp");
                uploaded.add(previewKey);
            }
        } catch (IOException e) {
            cleanup(uploaded);
            log.error("读取上传文件失败", e);
            throw new CustomException(500, "读取文件失败，请稍后重试");
        } catch (RuntimeException e) {
            cleanup(uploaded);
            log.error("图库上传对象失败 key={}", originalKey, e);
            // 网络层失败（DNS/连接超时/被墙）与逻辑错误分开提示，便于现场排查
            if (isNetworkFailure(e)) {
                throw new CustomException(500, "无法连接对象存储，请检查网络或代理配置（cloud.aws.s3.proxy）");
            }
            throw new CustomException(500, "图片上传失败，请稍后重试");
        }

        GalleryPhoto photo = new GalleryPhoto();
        photo.setFileKey(originalKey);
        photo.setFileName(originalName);
        photo.setTitle(resolveTitle(title, originalName));
        photo.setMimeType(contentTypeOf(ext));
        photo.setFileSize(file.getSize());
        photo.setWidth(width);
        photo.setHeight(height);
        photo.setThumbKey(thumbKey);
        photo.setPreviewKey(previewKey);
        photo.setUploaderId(uploaderId);
        photo.setStatus(STATUS_NORMAL);
        photo.setNsfw(0);
        photo.setAlbumId(albumId);
        photo.setCreateTime(LocalDateTime.now());

        try {
            galleryPhotoRepository.insert(photo);
        } catch (RuntimeException e) {
            cleanup(uploaded);
            log.error("图库写入数据库失败，已回删对象 key={}", originalKey, e);
            throw new CustomException(500, "图片保存失败，请稍后重试");
        }

        GalleryPhotoVO vo = toVO(photo);
        log.info("图库上传成功 photoId={} key={} {}x{}", photo.getPhotoId(), originalKey, width, height);
        return vo;
    }

    @Override
    public GalleryBatchUploadVO uploadBatch(List<MultipartFile> files, Long uploaderId, Long albumId) {
        if (files == null || files.isEmpty()) {
            throw new CustomException(400, "请选择要上传的图片");
        }
        if (files.size() > MAX_BATCH_SIZE) {
            throw new CustomException(400, "一次最多上传 " + MAX_BATCH_SIZE + " 张，请分批上传");
        }

        List<CompletableFuture<GalleryPhotoVO>> futures = files.stream()
                .map(file -> CompletableFuture.supplyAsync(() -> {
                    try {
                        return upload(file, null, uploaderId, albumId);
                    } catch (Exception e) {
                        // 用异常把原因带回主线程，避免在并发线程里拼结果
                        throw new UploadFailure(file.getOriginalFilename(), e.getMessage(), e);
                    }
                }, uploadExecutor))
                .toList();

        GalleryBatchUploadVO result = new GalleryBatchUploadVO();
        for (int i = 0; i < futures.size(); i++) {
            String fileName = files.get(i).getOriginalFilename();
            try {
                result.getSuccess().add(futures.get(i).join());
            } catch (Exception e) {
                Throwable cause = e instanceof java.util.concurrent.CompletionException && e.getCause() != null
                        ? e.getCause() : e;
                String reason = cause instanceof UploadFailure ? cause.getMessage() : "上传失败";
                result.getFailed().add(new GalleryBatchUploadVO.FailedItem(fileName, reason));
                log.warn("批量上传失败 file={} reason={}", fileName, reason);
            }
        }
        log.info("批量上传完成：成功 {} 张，失败 {} 张", result.getSuccess().size(), result.getFailed().size());
        return result;
    }

    @jakarta.annotation.PreDestroy
    public void shutdownExecutor() {
        uploadExecutor.shutdown();
        try {
            if (!uploadExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                uploadExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            uploadExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /** 批量上传里单张失败时携带文件名与原因的包装异常 */
    private static class UploadFailure extends RuntimeException {
        UploadFailure(String fileName, String reason, Throwable cause) {
            super(reason == null ? "上传失败：" + fileName : reason, cause);
        }
    }

    @Override
    public void moveToAlbum(Long photoId, Long albumId) {
        GalleryPhoto photo = galleryPhotoRepository.selectById(photoId);
        if (photo == null) {
            throw new CustomException(404, "图片不存在");
        }
        if (albumId != null && galleryAlbumRepository.selectById(albumId) == null) {
            throw new CustomException(404, "相册不存在");
        }
        galleryPhotoRepository.updateAlbum(photoId, albumId);
        // 照片换了相册（或移出），原相册若拿它当封面就清掉，否则封面会指向别的相册的图
        if (photo.getAlbumId() != null && !photo.getAlbumId().equals(albumId)) {
            galleryAlbumRepository.clearCoverByPhotoId(photoId);
        }
        log.info("图库照片归册 photoId={} albumId={}", photoId, albumId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int moveToAlbumBatch(List<Long> photoIds, Long albumId) {
        if (photoIds == null || photoIds.isEmpty()) {
            throw new CustomException(400, "请先选择图片");
        }
        if (albumId != null && galleryAlbumRepository.selectById(albumId) == null) {
            throw new CustomException(404, "相册不存在");
        }
        // 列表是去重过的，但前端可能传重复 id，这里再收一次，避免无谓的行锁
        List<Long> ids = photoIds.stream().filter(Objects::nonNull).distinct().toList();
        if (ids.isEmpty()) {
            throw new CustomException(400, "请先选择图片");
        }
        if (ids.size() > BATCH_MOVE_LIMIT) {
            throw new CustomException(400, "一次最多移动 " + BATCH_MOVE_LIMIT + " 张，请分批操作");
        }

        int moved = galleryPhotoRepository.updateAlbumBatch(ids, albumId);
        // 原相册的封面若指向这批图就断开；目标相册自己的封面留着
        galleryAlbumRepository.clearCoverByPhotoIds(ids, albumId);
        log.info("图库批量归册 {} 张 albumId={}", moved, albumId);
        return moved;
    }

    @Override
    public void updatePhoto(GalleryPhotoUpdateDTO updateDTO) {
        GalleryPhoto photo = galleryPhotoRepository.selectById(updateDTO.getPhotoId());
        if (photo == null) {
            throw new CustomException(404, "图片不存在");
        }
        // 两个字段都没传时直接返回，避免生成 set 为空的 update 语句
        if (updateDTO.getTitle() == null && updateDTO.getAlbumId() == null && updateDTO.getNsfw() == null) {
            return;
        }
        galleryPhotoRepository.updateMeta(updateDTO.getPhotoId(), updateDTO.getTitle(),
                updateDTO.getAlbumId(), updateDTO.getNsfw());
    }

    @Override
    public void restore(Long photoId) {
        GalleryPhoto photo = galleryPhotoRepository.selectById(photoId);
        if (photo == null) {
            throw new CustomException(404, "图片不存在");
        }
        if (Integer.valueOf(STATUS_NORMAL).equals(photo.getStatus())) {
            return;
        }
        galleryPhotoRepository.updateStatus(photoId, STATUS_NORMAL);
        log.info("图库恢复 photoId={}", photoId);
    }

    @Override
    public void setHidden(Long photoId, boolean hidden) {
        GalleryPhoto photo = galleryPhotoRepository.selectById(photoId);
        if (photo == null) {
            throw new CustomException(404, "图片不存在");
        }
        if (Integer.valueOf(STATUS_RECYCLED).equals(photo.getStatus())) {
            throw new CustomException(400, "回收站中的图片请先恢复再操作");
        }
        int target = hidden ? STATUS_HIDDEN : STATUS_NORMAL;
        if (Integer.valueOf(target).equals(photo.getStatus())) {
            return;
        }
        galleryPhotoRepository.updateStatus(photoId, target);
        log.info("图库{} photoId={}", hidden ? "隐藏" : "取消隐藏", photoId);
    }

    @Override
    public void softDelete(Long photoId) {
        GalleryPhoto photo = galleryPhotoRepository.selectById(photoId);
        if (photo == null) {
            throw new CustomException(404, "图片不存在");
        }
        galleryPhotoRepository.updateStatus(photoId, STATUS_RECYCLED);
    }

    @Override
    public void purge(Long photoId) {
        GalleryPhoto photo = galleryPhotoRepository.selectById(photoId);
        if (photo == null) {
            throw new CustomException(404, "图片不存在");
        }
        // 先删记录再删对象：记录是唯一索引，删掉后即使对象删除失败也只是留下孤儿文件，不会出现坏图
        galleryPhotoRepository.deleteById(photoId);
        // 记录没了，相册的封面引用也要断开，否则封面查不到 thumb_key 却还占着位置
        galleryAlbumRepository.clearCoverByPhotoId(photoId);

        List<String> keys = new ArrayList<>(3);
        keys.add(photo.getFileKey());
        keys.add(photo.getThumbKey());
        if (photo.getPreviewKey() != null) {
            keys.add(photo.getPreviewKey());
        }
        for (String key : keys) {
            if (key != null && !key.isEmpty()) {
                r2FileService.deleteR2File(key);
            }
        }
        log.info("图库彻底删除 photoId={} keys={}", photoId, keys);
    }

    /** 上传目录：blog/{userId}/{upload.dir.gallery} —— 不再按年月分层 */
    private String baseDir(Long uploaderId) {
        String subDir = uploadConfig.getDir().getGallery();
        if (subDir == null || subDir.isBlank()) {
            subDir = "gallery/";
        }
        if (!subDir.endsWith("/")) {
            subDir += "/";
        }
        return "blog/" + uploaderId + "/" + subDir;
    }

    private String resolveTitle(String title, String originalName) {
        if (title != null && !title.isBlank()) {
            return title.trim();
        }
        int dot = originalName.lastIndexOf('.');
        return dot > 0 ? originalName.substring(0, dot) : originalName;
    }

    private String extensionOf(String name) {
        int dot = name.lastIndexOf('.');
        return dot >= 0 ? name.substring(dot + 1).toLowerCase(Locale.ROOT) : "";
    }

    private String contentTypeOf(String ext) {
        return switch (ext) {
            case "png" -> "image/png";
            case "webp" -> "image/webp";
            case "bmp" -> "image/bmp";
            default -> "image/jpeg";
        };
    }

    private BufferedImage readImage(MultipartFile file) {
        try (InputStream in = file.getInputStream()) {
            BufferedImage image = ImageIO.read(in);
            if (image == null) {
                throw new CustomException(400, "无法解析该图片，请检查文件是否损坏");
            }
            return image;
        } catch (IOException e) {
            throw new CustomException(400, "无法解析该图片，请检查文件是否损坏");
        }
    }

    /** 判断是否是连不上对象存储（超时/DNS/拒绝连接）这类网络问题 */
    private boolean isNetworkFailure(Throwable e) {
        Throwable cursor = e;
        while (cursor != null) {
            if (cursor instanceof java.net.SocketTimeoutException
                    || cursor instanceof java.net.ConnectException
                    || cursor instanceof java.net.UnknownHostException
                    || cursor instanceof javax.net.ssl.SSLException) {
                return true;
            }
            String msg = cursor.getMessage();
            if (msg != null && msg.contains("Unable to execute HTTP request")) {
                return true;
            }
            cursor = cursor.getCause();
        }
        return false;
    }

    private void cleanup(List<String> keys) {
        for (String key : keys) {
            try {
                r2FileService.deleteR2File(key);
            } catch (Exception e) {
                log.warn("回删图库对象失败 key={}", key, e);
            }
        }
    }

    /** Entity → VO，并拼好三个 URL；小图没有预览图时回退原图 */
    private GalleryPhotoVO toVO(GalleryPhoto entity) {
        GalleryPhotoVO vo = galleryPhotoConvert.toVO(entity);
        vo.setOriginalUrl(r2FileService.buildUrl(entity.getFileKey()));
        vo.setThumbUrl(r2FileService.buildUrl(entity.getThumbKey()));
        String previewKey = entity.getPreviewKey();
        vo.setPreviewUrl(r2FileService.buildUrl(previewKey == null || previewKey.isEmpty()
                ? entity.getFileKey() : previewKey));
        return vo;
    }
}
