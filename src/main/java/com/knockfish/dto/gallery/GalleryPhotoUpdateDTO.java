package com.knockfish.dto.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "图库照片更新信息")
public class GalleryPhotoUpdateDTO {
    @NotNull(message = "照片ID不能为空")
    @Schema(description = "照片ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long photoId;

    @Size(max = 100, message = "标题长度不能超过100")
    @Schema(description = "展示标题")
    private String title;

    @Schema(description = "相册ID（预留）")
    private Long albumId;

    @Schema(description = "是否敏感：0 普通 / 1 敏感", example = "1")
    private Integer nsfw;
}
