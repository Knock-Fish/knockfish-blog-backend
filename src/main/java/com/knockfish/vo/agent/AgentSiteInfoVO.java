package com.knockfish.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Agent博客站点基础信息VO")
public class AgentSiteInfoVO {
    @Schema(description = "站点名称")
    private String siteName;

    @Schema(description = "站点描述")
    private String siteDescription;

    @Schema(description = "已发布文章总数")
    private Long articleCount;

    @Schema(description = "标签总数")
    private Long tagCount;

    @Schema(description = "分类总数")
    private Long categoryCount;

    @Schema(description = "笔记总数")
    private Long noteCount;

    @Schema(description = "代码片段总数")
    private Long codeSnippetCount;

    @Schema(description = "友链总数")
    private Long linkCount;

    @Schema(description = "导航站点总数")
    private Long siteCount;
}
