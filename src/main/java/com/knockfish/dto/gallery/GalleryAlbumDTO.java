package com.knockfish.dto.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 相册新建 / 编辑入参。新建时 albumId 不传。
 *
 * <p>封面不在这里改：它只能从相册内的照片里挑，走 {@code PUT /album/{id}/cover}。
 */
@Data
@Schema(description = "图库相册入参")
public class GalleryAlbumDTO {

    @Schema(description = "相册ID，编辑时必填")
    private Long albumId;

    @Schema(description = "相册名", example = "旅行")
    @NotBlank(message = "相册名不能为空")
    @Size(max = 64, message = "相册名不能超过 64 个字符")
    private String name;

    @Schema(description = "描述")
    @Size(max = 255, message = "描述不能超过 255 个字符")
    private String description;

    @Schema(description = "排序，越小越靠前", example = "10")
    private Integer sortOrder;

    @Schema(description = "0 正常 / 1 隐藏；不传默认 0")
    private Integer status;
}
