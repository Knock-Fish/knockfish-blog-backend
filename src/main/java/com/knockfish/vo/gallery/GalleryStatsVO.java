package com.knockfish.vo.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 图库统计（只统计正常状态的照片），用于顶部标签的数字与总占用。
 */
@Data
@Schema(description = "图库统计")
public class GalleryStatsVO {
    @Schema(description = "总数")
    private Long allCount;

    @Schema(description = "横图数")
    private Long landscape;

    @Schema(description = "竖图数")
    private Long portrait;

    @Schema(description = "方图数")
    private Long square;

    @Schema(description = "原图总字节数")
    private Long totalSize;

    @Schema(description = "回收站数量")
    private Long trashCount;

    @Schema(description = "隐藏数量")
    private Long hiddenCount;

    @Schema(description = "未归册数量（正常状态且不属于任何相册）")
    private Long unassignedCount;
}
