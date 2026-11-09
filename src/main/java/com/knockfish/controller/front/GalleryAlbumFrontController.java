package com.knockfish.controller.front;

import com.knockfish.common.Result;
import com.knockfish.dto.gallery.AlbumUnlockDTO;
import com.knockfish.service.GalleryAlbumService;
import com.knockfish.utils.IpUtil;
import com.knockfish.vo.gallery.AlbumUnlockVO;
import com.knockfish.vo.gallery.GalleryAlbumVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 前台相册列表：匿名可读。
 * 私密相册只返回名字与 locked 标记，封面与照片数一律不给；
 * 拿照片前必须先调 unlock 换令牌。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/gallery/albums")
@Tag(name = "前台图库相册", description = "相册列表、详情与解锁")
public class GalleryAlbumFrontController {

    private final GalleryAlbumService galleryAlbumService;

    @GetMapping
    @Operation(summary = "相册列表")
    public Result<List<GalleryAlbumVO>> list() {
        return Result.success(galleryAlbumService.getPublicAlbumList());
    }

    @PostMapping("/{id:\\d+}/unlock")
    @Operation(summary = "解锁私密相册", description = "校验验证码与密码，通过后返回访问令牌")
    public Result<AlbumUnlockVO> unlock(
            @Parameter(description = "相册ID") @PathVariable Long id,
            @Valid @RequestBody AlbumUnlockDTO dto,
            HttpServletRequest request) {
        return Result.success(galleryAlbumService.unlock(id, dto, IpUtil.resolve(request)));
    }

    @GetMapping("/{id:\\d+}")
    @Operation(summary = "相册详情")
    public Result<GalleryAlbumVO> detail(@Parameter(description = "相册ID") @PathVariable Long id) {
        return Result.success(galleryAlbumService.getPublicAlbum(id));
    }
}
