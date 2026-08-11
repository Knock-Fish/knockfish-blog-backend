package com.knockfish.dto.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "站点更新请求DTO")
public class SiteUpdateDTO {
    @Schema(description = "站点ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long siteId;
    @Schema(description = "站点名称", example = "GitHub")
    private String siteName;
    @Schema(description = "站点URL", example = "https://github.com")
    private String siteUrl;
    @Schema(description = "站点描述", example = "代码托管平台")
    private String description;
    @Schema(description = "站点图标URL", example = "https://github.com/favicon.ico")
    private String ico;
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
}
