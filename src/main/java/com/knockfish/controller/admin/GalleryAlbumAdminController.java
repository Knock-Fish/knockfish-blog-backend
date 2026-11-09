package com.knockfish.controller.admin;

import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.dto.gallery.AlbumPasswordDTO;
import com.knockfish.dto.gallery.GalleryAlbumDTO;
import com.knockfish.service.GalleryAlbumService;
import com.knockfish.vo.gallery.GalleryAlbumVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 后台图库相册管理。照片分配到相册走 GalleryAdminController 的 /{id}/album。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/gallery/album")
@Tag(name = "图库相册", description = "相册增删改查")
public class GalleryAlbumAdminController {

    private final GalleryAlbumService galleryAlbumService;

    @GetMapping
    @Operation(summary = "相册列表", description = "后台可见全部状态；返回封面与照片数量")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:album')")
    public Result<List<GalleryAlbumVO>> list(
            @Parameter(description = "0 正常 / 1 隐藏，不传查全部") @RequestParam(required = false) Integer status,
            @Parameter(description = "相册名关键字") @RequestParam(required = false) String keyword) {
        return Result.success(galleryAlbumService.getAlbumList(status, keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "相册详情")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:album')")
    public Result<GalleryAlbumVO> detail(@Parameter(description = "相册ID") @PathVariable Long id) {
        return Result.success(galleryAlbumService.getAlbum(id));
    }

    @PostMapping
    @Operation(summary = "新建相册")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:album')")
    @Log("新建图库相册")
    public Result<GalleryAlbumVO> create(@Valid @RequestBody GalleryAlbumDTO dto) {
        return Result.success(galleryAlbumService.create(dto));
    }

    @PutMapping
    @Operation(summary = "编辑相册", description = "名称、描述、封面、排序、显隐；密码另有接口")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:album')")
    @Log("编辑图库相册")
    public Result<Void> update(@Valid @RequestBody GalleryAlbumDTO dto) {
        galleryAlbumService.update(dto);
        return Result.success();
    }

    @PutMapping("/{id}/cover")
    @Operation(summary = "设置 / 清除封面",
            description = "photoId 为空表示清除封面；只能选本相册里的照片，展示它的缩略图；私密相册不允许设置")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:album')")
    @Log("设置相册封面")
    public Result<Void> setCover(
            @Parameter(description = "相册ID") @PathVariable Long id,
            @Parameter(description = "照片ID，不传表示清除封面") @RequestParam(required = false) Long photoId) {
        galleryAlbumService.setCover(id, photoId);
        return Result.success();
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "设置 / 清除密码", description = "body 里 password 留空表示清除密码，相册变公开")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:album')")
    @Log("设置相册密码")
    public Result<Void> setPassword(
            @Parameter(description = "相册ID") @PathVariable Long id,
            @RequestBody AlbumPasswordDTO dto) {
        galleryAlbumService.setPassword(id, dto.getPassword());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除相册", description = "相册内照片只解绑，不会被删除")
    @PreAuthorize("hasAuthority('PERMISSION_blog:gallery:album')")
    @Log("删除图库相册")
    public Result<Void> delete(@Parameter(description = "相册ID") @PathVariable Long id) {
        galleryAlbumService.delete(id);
        return Result.success();
    }
}
