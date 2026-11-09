package com.knockfish.controller.admin;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.dto.gallery.GalleryBatchAlbumDTO;
import com.knockfish.dto.gallery.GalleryPhotoQueryDTO;
import com.knockfish.dto.gallery.GalleryPhotoUpdateDTO;
import com.knockfish.exception.CustomException;
import com.knockfish.security.CustomUserDetails;
import com.knockfish.service.GalleryPhotoService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.gallery.GalleryBatchUploadVO;
import com.knockfish.vo.gallery.GalleryPhotoVO;
import com.knockfish.vo.gallery.GalleryStatsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 后台图库接口。
 * 上传/删除走独立权限码；查询用 manage。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/gallery")
@Tag(name = "图库管理", description = "图库照片上传、查询、删除")
public class GalleryAdminController {

    private final GalleryPhotoService galleryPhotoService;

    @GetMapping
    @Operation(summary = "分页查询图库", description = "支持关键字、朝向、状态筛选；默认查正常状态")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:manage')")
    @Log("查询图库列表")
    public Result<PageResultVO<GalleryPhotoVO>> getPhotoPage(
            @Parameter(description = "查询条件") GalleryPhotoQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "20") Integer pageSize) {
        // 限制单页上限，避免被一次性拉全表
        PageInfo<GalleryPhotoVO> pageInfo = galleryPhotoService.getPhotoPage(query, pageNum, Math.min(Math.max(pageSize, 1), 60));
        return Result.success(PageConvertUtil.convert(pageInfo));
    }

    @GetMapping("/stats")
    @Operation(summary = "图库统计", description = "各朝向数量与原图总大小；传 albumId 或 onlyUnassigned 时按当前视图统计")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:manage')")
    public Result<GalleryStatsVO> getStats(
            @Parameter(description = "相册ID，只统计该相册") @RequestParam(required = false) Long albumId,
            @Parameter(description = "true 只统计未归册") @RequestParam(required = false) Boolean onlyUnassigned) {
        return Result.success(galleryPhotoService.getStats(albumId, onlyUnassigned));
    }

    @PostMapping
    @Operation(summary = "上传图片", description = "上传单张图片，自动生成缩略图与预览图；保留原图")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:upload')")
    @Log("上传图库图片")
    public Result<GalleryPhotoVO> upload(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file,
            @Parameter(description = "标题，缺省用文件名") @RequestParam(required = false) String title,
            @Parameter(description = "所属相册ID，不传则未归册") @RequestParam(required = false) Long albumId) {
        Long userId = currentUserId();
        return Result.success(galleryPhotoService.upload(file, title, userId, albumId));
    }

    @PostMapping("/batch")
    @Operation(summary = "批量上传图片", description = "一次最多 10 张，并发处理；单张失败不影响其它文件")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:upload')")
    @Log("批量上传图库图片")
    public Result<GalleryBatchUploadVO> uploadBatch(
            @Parameter(description = "图片文件列表") @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "所属相册ID，不传则未归册") @RequestParam(required = false) Long albumId) {
        Long userId = currentUserId();
        return Result.success(galleryPhotoService.uploadBatch(files, userId, albumId));
    }

    @PutMapping
    @Operation(summary = "修改图片信息", description = "目前只支持改标题与相册")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:manage')")
    @Log("修改图库图片")
    public Result<Void> update(@Valid @RequestBody GalleryPhotoUpdateDTO updateDTO) {
        galleryPhotoService.updatePhoto(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "移入回收站", description = "软删除，保留 R2 对象，可用彻底删除清理")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:delete')")
    @Log("图库移入回收站")
    public Result<Void> softDelete(@Parameter(description = "照片ID") @PathVariable Long id) {
        galleryPhotoService.softDelete(id);
        return Result.success();
    }

    @PutMapping("/{id}/restore")
    @Operation(summary = "从回收站恢复", description = "状态改回正常，前台立即可见")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:delete')")
    @Log("图库恢复")
    public Result<Void> restore(@Parameter(description = "照片ID") @PathVariable Long id) {
        galleryPhotoService.restore(id);
        return Result.success();
    }

    @PutMapping("/{id}/hidden")
    @Operation(summary = "隐藏 / 取消隐藏", description = "隐藏后前台不展示（列表与详情都取不到），后台仍可管理")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:manage')")
    @Log("图库隐藏切换")
    public Result<Void> setHidden(
            @Parameter(description = "照片ID") @PathVariable Long id,
            @Parameter(description = "true 隐藏 / false 取消隐藏") @RequestParam Boolean hidden) {
        galleryPhotoService.setHidden(id, Boolean.TRUE.equals(hidden));
        return Result.success();
    }

    @PutMapping("/batch/album")
    @Operation(summary = "批量移动到相册",
            description = "把选中的多张图片一次搬到目标相册；albumId 不传表示全部移出相册，一次最多 200 张")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:manage')")
    @Log("图库批量移动")
    public Result<Integer> moveToAlbumBatch(@Valid @RequestBody GalleryBatchAlbumDTO dto) {
        return Result.success(galleryPhotoService.moveToAlbumBatch(dto.getPhotoIds(), dto.getAlbumId()));
    }

    @PutMapping("/{id}/album")
    @Operation(summary = "移动到相册", description = "albumId 不传表示移出相册")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:manage')")
    @Log("图库移动到相册")
    public Result<Void> moveToAlbum(
            @Parameter(description = "照片ID") @PathVariable Long id,
            @Parameter(description = "相册ID，不传表示移出相册") @RequestParam(required = false) Long albumId) {
        galleryPhotoService.moveToAlbum(id, albumId);
        return Result.success();
    }

    @DeleteMapping("/{id}/purge")
    @Operation(summary = "彻底删除", description = "删除原图/缩略图/预览图三个对象并删除记录")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:delete')")
    @Log("图库彻底删除")
    public Result<Void> purge(@Parameter(description = "照片ID") @PathVariable Long id) {
        galleryPhotoService.purge(id);
        return Result.success();
    }

    private Long currentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomException(401, "用户未登录");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
    }
}
