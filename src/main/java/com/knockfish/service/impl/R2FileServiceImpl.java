package com.knockfish.service.impl;

import com.knockfish.config.R2FileConfig;
import com.knockfish.config.UploadConfig;
import com.knockfish.dto.file_reference.FileReferenceCreateDTO;
import com.knockfish.dto.r2_file.R2FileUploadDTO;
import com.knockfish.service.FileReferenceService;
import com.knockfish.service.R2FileService;
import com.knockfish.utils.FileUtil;
import com.knockfish.utils.ImageWebpProcessor;
import com.knockfish.vo.r2_file.R2FileInfoVO;
import com.knockfish.vo.r2_file.R2FileListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class R2FileServiceImpl implements R2FileService {
    private final S3Client s3Client;
    private final R2FileConfig r2FileConfig;
    private final UploadConfig uploadConfig;
    private final FileReferenceService fileReferenceService;

    private static final List<String> IMG_SUFFIX = Arrays.asList("jpg", "jpeg", "jpe", "png", "bmp", "webp");
    private static final DateTimeFormatter YM_FMT = DateTimeFormatter.ofPattern("yyyy-MM");

//    @Override
//    public Map<String, String> uploadR2File(MultipartFile file, String baseDir){
//        try {
//            // 支持的图片格式
//            List<String> imgSuffix = Arrays.asList("jpg", "jpeg", "jpe", "png", "bmp", "webp");
//            // 获取原始文件名
//            String originalFilename = file.getOriginalFilename();
//            // 文件后缀
//            String fileExtension;
//            String contentType;
//            byte[] uploadBytes;
//            String key;
//            int dotIndex = originalFilename.lastIndexOf(".");
//            if(dotIndex >= 0){
//                fileExtension = originalFilename.substring(dotIndex + 1);
//            }else{
//                fileExtension = "";
//            }
//            // 生成年月格式：2025-09
//            String yearMonthFolder = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
//            // 固定前缀 + 年月文件夹
//            String baseFolder = "blog/article-image/" + yearMonthFolder + "/";
//
//            // 判断是否为图片文件
//            if(imgSuffix.contains(fileExtension)){
//                // 转换为WebP格式
//                uploadBytes = ImageWebpProcessor.convertImageToWebp(file);
//                // 存到指定文件夹
//                key = baseFolder + UUID.randomUUID() + ".webp";
//                contentType = "image/webp";
//            }else{
//                // 非图片直接使用原文件字节
//                uploadBytes = file.getBytes();
//                // 使用原始文件名作为key
//                key = baseFolder + originalFilename;
//                // 原始文件MIME类型
//                contentType = file.getContentType();
//            }
//            // 构建上传请求
//            PutObjectRequest putRequest = PutObjectRequest.builder()
//                    .bucket(r2FileConfig.getBucketName())   // R2 存储桶名称
//                    .key(key)   // 文件在 R2 中的路径
//                    .contentType(contentType) // 设置 MIME 类型（如 image/jpeg）
//                    .build();
//
//
//            // 执行上传
//            s3Client.putObject(putRequest, RequestBody.fromBytes(uploadBytes));
//
//            // 生成可访问URL（含CDN域名）
//            Map<String, String> map = new HashMap<>();
//            map.put("url", r2FileConfig.getCdnDomain() + "/" + key);
//            map.put("key", key);
//            return map;
//        }catch (S3Exception e){
//            log.error("R2文件上传失败: {}", e.getMessage(), e);
//            throw new RuntimeException("文件上传失败，请稍后再试", e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
@Override
public Map<String, Object> uploadR2File(R2FileUploadDTO uploadDTO) {
    MultipartFile file = uploadDTO.getFile();
    String baseFolder = getString(uploadDTO);
    FileReferenceCreateDTO fileReferenceCreateDTO = new FileReferenceCreateDTO();

    try {
        String originalFilename = file.getOriginalFilename();
        int dotIndex = originalFilename.lastIndexOf(".");
        String fileExtension = dotIndex >= 0 ? originalFilename.substring(dotIndex + 1).toLowerCase() : "";

        byte[] uploadBytes;
        String key;
        String contentType;

        // 图片统一转WebP
        if (IMG_SUFFIX.contains(fileExtension)) {
            uploadBytes = ImageWebpProcessor.convertImageToWebp(file);
            key = baseFolder + UUID.randomUUID() + ".webp";
            contentType = "image/webp";
        } else {
            uploadBytes = file.getBytes();
            // 保留原始文件名，但需要处理重名问题（加时间戳或UUID）
            String timestamp = String.valueOf(System.currentTimeMillis());
            String nameWithoutExt = originalFilename.substring(0, dotIndex);
            String safeName = nameWithoutExt + "_" + timestamp + "." + fileExtension;
            key = baseFolder + safeName;
            contentType = file.getContentType();
        }

        // 构建R2上传请求
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(r2FileConfig.getBucketName())
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(putRequest, RequestBody.fromBytes(uploadBytes));

        // 拼接CDN访问链接
        String fullUrl = r2FileConfig.getCdnDomain() + "/" + key;
        Map<String, Object> result = new HashMap<>(2);
        fileReferenceCreateDTO.setFilePath(key);
        fileReferenceCreateDTO.setFileName(originalFilename);
        fileReferenceCreateDTO.setMimeType(contentType);
        fileReferenceCreateDTO.setReferenceType(uploadDTO.getType());
        fileReferenceCreateDTO.setUserId(uploadDTO.getUserId());
        fileReferenceCreateDTO.setFileSize(file.getSize());
        if(Objects.equals(uploadDTO.getType(), "avatar")){
            fileReferenceCreateDTO.setReferenceId(uploadDTO.getUserId());
        } else if(uploadDTO.getReferenceId() != null){
            // 文章/笔记等：上传时直接携带关联ID，立即建立关联
            fileReferenceCreateDTO.setReferenceId(uploadDTO.getReferenceId());
        }
        Long fileId = fileReferenceService.createReference(fileReferenceCreateDTO);
        result.put("url", fullUrl);
        result.put("key", key);
        result.put("fileId", fileId);

        return result;

    } catch (S3Exception e) {
        log.error("R2上传异常 S3错误: {}", e.awsErrorDetails().errorMessage(), e);
        throw new RuntimeException("文件上传R2失败，请稍后重试", e);
    } catch (IOException e) {
        log.error("读取文件字节失败", e);
        throw new RuntimeException("读取文件失败", e);
    }
}

    private String getString(R2FileUploadDTO uploadDTO) {
        String type = uploadDTO.getType();
        Long userId = uploadDTO.getUserId();

        // 读取配置子目录
        String subDir = switch (type) {
            case "avatar" -> uploadConfig.getDir().getAvatar();
            case "cover" ->uploadConfig.getDir().getCover();
            case "article" -> uploadConfig.getDir().getArticle();
            case "note" -> uploadConfig.getDir().getNote();
            case "background" -> uploadConfig.getDir().getBackground();
            default -> throw new RuntimeException("非法上传类型：" + type);
        };

        // 统一路径：blog/{userId}/{type}/
        // 例如：blog/1001/avatar/, blog/1001/article/, blog/1001/note/
        return "blog/" + userId + "/" + subDir;
    }

    @Override
    public String getR2FileUrl(String key) {
        try {
            // 使用ListObjectsV2来查找包含该文件名的对象
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(r2FileConfig.getBucketName())
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            // 遍历所有对象，查找匹配的文件
            for (S3Object object : response.contents()) {
                if (object.key().endsWith(key)) {
                    // 找到匹配的对象，返回完整路径
                    return r2FileConfig.getCdnDomain() + "/" + object.key();
                }
            }

            // 如果没有找到匹配的对象，抛出异常
            throw new RuntimeException("未找到指定的文件: " + key);

        } catch (S3Exception e) {
            log.error("获取对象URL失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取对象URL失败", e);
        }
    }

    @Override
    public List<String> getAllR2Files() {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(r2FileConfig.getBucketName())
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            List<String> objectKeys = new ArrayList<>();

            for (S3Object object : response.contents()) {
                objectKeys.add(object.key());
            }

            return objectKeys;
        } catch (S3Exception e) {
            log.error("获取对象列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取对象列表失败", e);
        }
    }

    @Override
    public List<R2FileListVO> getAllR2FilesByPrefix(String prefix) {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(r2FileConfig.getBucketName())
                    .prefix(prefix)
                    .delimiter("/")
                    .build();
            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            List<R2FileListVO> result = new ArrayList<>();

            // 获取文件夹 - CommonPrefix 对象需要获取其 prefix 值
            for (CommonPrefix commonPrefix : response.commonPrefixes()) {
                String folderKey = commonPrefix.prefix();
                R2FileListVO vo = R2FileListVO.builder()
                        .key(folderKey)
                        .size(0L)         // 文件夹大小为0
                        .sizeFormat("-")  // 显示 -
                        .lastModified(null)
                        .build();
                result.add(vo);  // 注意：方法名是 prefix() 小写
            }

            // 获取文件（当前层级的直接子文件）
            for (S3Object object : response.contents()) {
                String fileKey = object.key();
                // 过滤掉自身（空目录）
                if (fileKey.equals(prefix)) {
                    continue;
                }
                R2FileListVO vo = R2FileListVO.builder()
                        .key(fileKey)
                        .size(object.size())
                        .sizeFormat(FileUtil.formatFileSize(object.size()))
                        .lastModified(object.lastModified())
                        .build();
                result.add(vo);
            }
            return result;
        } catch (S3Exception e) {
            log.error("获取对象列表失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取对象列表失败", e);
        }
    }

    @Override
    public R2FileInfoVO getR2FileInfo(String key) {
        try {
            // 使用ListObjectsV2来查找包含该文件名的对象
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(r2FileConfig.getBucketName())
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            // 遍历所有对象，查找匹配的文件
            for (S3Object object : response.contents()) {
                if (object.key().endsWith(key)) {
                    // 找到匹配的对象，返回详细信息
                    return R2FileInfoVO.builder()
                            .key(object.key())
                            .url(r2FileConfig.getCdnDomain() + "/" + object.key())
                            .size(object.size())
                            .sizeFormat(FileUtil.formatFileSize(object.size()))
                            .lastModified(object.lastModified())
                            .build();
                }
            }

            // 如果没有找到匹配的对象，抛出异常
            throw new RuntimeException("未找到指定的文件: " + key);

        } catch (S3Exception e) {
            log.error("获取对象信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取对象信息失败", e);
        }
    }

    @Override
    public boolean deleteR2File(String key) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(r2FileConfig.getBucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(request);
            return true;
        } catch (S3Exception e) {
            log.error("删除对象失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean batchDeleteR2File(List<String> keys) {
        try {
            for(String key : keys){
                DeleteObjectRequest request = DeleteObjectRequest.builder()
                        .bucket(r2FileConfig.getBucketName())
                        .key(key)
                        .build();

                s3Client.deleteObject(request);
            }
            return true;
        } catch (S3Exception e) {
            log.error("删除对象失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
