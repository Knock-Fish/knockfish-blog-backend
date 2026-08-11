package com.knockfish.dto.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "站点创建请求DTO")
public class SiteCreateDTO {
    @Schema(description = "站点名称", example = "GitHub", requiredMode = Schema.RequiredMode.REQUIRED)
    private String siteName;
    @Schema(description = "站点URL", example = "https://github.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String siteUrl;
    @Schema(description = "站点描述", example = "代码托管平台")
    private String description;
    @Schema(description = "站点图标URL", example = "https://github.com/favicon.ico")
    private String ico;
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
}
