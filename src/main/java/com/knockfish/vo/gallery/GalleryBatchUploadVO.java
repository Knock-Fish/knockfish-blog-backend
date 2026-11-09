package com.knockfish.vo.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 批量上传结果：成功的照片 + 失败的文件与原因。
 * 单张失败不影响同批其它文件，前端据此逐条提示。
 */
@Data
@Schema(description = "图库批量上传结果")
public class GalleryBatchUploadVO {

    @Schema(description = "上传成功的照片")
    private List<GalleryPhotoVO> success = new ArrayList<>();

    @Schema(description = "失败的文件")
    private List<FailedItem> failed = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "失败项")
    public static class FailedItem {
        @Schema(description = "文件名")
        private String fileName;

        @Schema(description = "失败原因")
        private String reason;
    }
}
