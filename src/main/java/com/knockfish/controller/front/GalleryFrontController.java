package com.knockfish.controller.front;

import com.github.pagehelper.PageInfo;
import com.knockfish.common.Result;
import com.knockfish.dto.gallery.GalleryPhotoQueryDTO;
import com.knockfish.entity.GalleryAlbum;
import com.knockfish.exception.CustomException;
import com.knockfish.repository.GalleryAlbumRepository;
import com.knockfish.service.AlbumTokenService;
import com.knockfish.service.GalleryPhotoService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.gallery.GalleryPhotoVO;
import com.knockfish.vo.gallery.GalleryStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台图库只读接口（匿名可访问）。
 *
 * <p>照片本身只返回 status = 0；如果照片属于「私密相册」，还必须带上
 * 解锁后拿到的 {@code X-Album-Token}，否则返回业务码 40101。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/gallery")
@Tag(name = "前台-图库", description = "博客前台图库接口（匿名可访问）")
public class GalleryFrontController {

    private static final int STATUS_NORMAL = 0;
    /** 需要解锁：前端据此弹密码框，而不是当成未登录 */
    private static final int CODE_ALBUM_LOCKED = 40101;

    private final GalleryPhotoService galleryPhotoService;
    private final GalleryAlbumRepository galleryAlbumRepository;
    private final AlbumTokenService albumTokenService;

    @GetMapping
    @Operation(summary = "分页查询图库", description = "只返回正常状态的照片；私密相册需带 X-Album-Token")
    public Result<PageResultVO<GalleryPhotoVO>> getPhotoPage(
            @Parameter(description = "查询条件") GalleryPhotoQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize,
            @Parameter(description = "相册访问令牌") @RequestHeader(value = "X-Album-Token", required = false) String albumToken) {
        // 前台强制只查正常状态，忽略外部传入的 status
        query.setStatus(STATUS_NORMAL);
        // 指定相册时先校验访问权；未解锁直接拒绝，一条数据都不返回
        ensureAlbumAccess(query.getAlbumId(), albumToken);
        // 「全部」视图只聚合未归册 + 公开相册的照片
        query.setExcludeLockedAlbum(true);

        PageInfo<GalleryPhotoVO> pageInfo = galleryPhotoService.getPhotoPage(
                query, pageNum, Math.min(Math.max(pageSize, 1), 60));
        return Result.success(PageConvertUtil.convert(pageInfo));
    }

    @GetMapping("/stats")
    @Operation(summary = "图库统计", description = "各朝向数量，用于筛选标签的数字；传 albumId 时只统计该相册")
    public Result<GalleryStatsVO> getStats(
            @Parameter(description = "相册ID，不传统计全部") @RequestParam(required = false) Long albumId,
            @Parameter(description = "相册访问令牌") @RequestHeader(value = "X-Album-Token", required = false) String albumToken) {
        // 相册里的张数也算信息，私密相册未解锁时不给统计
        ensureAlbumAccess(albumId, albumToken);
        return Result.success(galleryPhotoService.getStats(albumId, null));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "获取图片详情", description = "仅返回正常状态的照片；私密相册需带 X-Album-Token")
    public Result<GalleryPhotoVO> getPhotoById(
            @Parameter(description = "照片ID") @PathVariable Long id,
            @Parameter(description = "相册访问令牌") @RequestHeader(value = "X-Album-Token", required = false) String albumToken) {
        GalleryPhotoVO photo = galleryPhotoService.getPhotoById(id, true);
        ensureAlbumAccess(photo.getAlbumId(), albumToken);
        return Result.success(photo);
    }

    /**
     * 相册访问校验：公开相册直接放行；私密相册要求令牌与该相册、当前密码版本匹配。
     */
    private void ensureAlbumAccess(Long albumId, String albumToken) {
        if (albumId == null) {
            return;
        }
        GalleryAlbum album = galleryAlbumRepository.selectById(albumId);
        if (album == null || !Integer.valueOf(STATUS_NORMAL).equals(album.getStatus())) {
            throw new CustomException(404, "相册不存在");
        }
        if (album.getPasswordHash() == null) {
            return;
        }
        int ver = album.getPasswordVer() == null ? 0 : album.getPasswordVer();
        if (!albumTokenService.verify(albumToken, albumId, ver)) {
            throw new CustomException(CODE_ALBUM_LOCKED, "该相册已加密，请先输入密码");
        }
    }
}
