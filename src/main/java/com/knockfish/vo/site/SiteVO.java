package com.knockfish.vo.site;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "站点信息VO")
public class SiteVO {
    @Schema(description = "站点ID", example = "1")
    private Long siteId;
    @Schema(description = "站点名称", example = "示例站点")
    private String siteName;
    @Schema(description = "站点URL", example = "https://example.com")
    private String siteUrl;
    @Schema(description = "站点描述", example = "这是一个示例站点")
    private String description;
    @Schema(description = "站点图标URL", example = "https://example.com/favicon.ico")
    private String ico;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @Schema(description = "分类ID", example = "1")
    private String categoryId;
}
