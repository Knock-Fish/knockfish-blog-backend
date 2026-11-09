package com.knockfish.dto.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 批量归册入参。把选中的多张图片一次性移到目标相册，或全部移出相册。
 */
@Data
@Schema(description = "图库批量归册入参")
public class GalleryBatchAlbumDTO {

    @Schema(description = "照片ID列表")
    @NotEmpty(message = "请先选择图片")
    @Size(max = 200, message = "一次最多移动 200 张")
    private List<Long> photoIds;

    @Schema(description = "目标相册ID；不传表示移出相册（不归入任何相册）")
    private Long albumId;
}
