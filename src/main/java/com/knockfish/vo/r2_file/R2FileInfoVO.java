package com.knockfish.vo.r2_file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@Schema(description = "R2文件信息VO")
public class R2FileInfoVO {
    @Schema(description = "文件在R2中的键", example = "uploads/2024/01/test.jpg")
    private String key;
    @Schema(description = "文件的访问URL", example = "https://example.com/r2/uploads/2024/01/test.jpg")
    private String url;
    @Schema(description = "文件大小（字节）", example = "1024000")
    private Long size;
    @Schema(description = "格式化后的文件大小", example = "1.0 MB")
    private String sizeFormat;
    @Schema(description = "最后修改时间")
    private Instant lastModified;
}
