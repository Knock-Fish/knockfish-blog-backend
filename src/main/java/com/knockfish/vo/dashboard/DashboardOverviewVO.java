package com.knockfish.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "仪表盘概览VO")
public class DashboardOverviewVO {

    @Schema(description = "总访问次数", example = "9120")
    private Long totalVisits;

    @Schema(description = "文章总数量", example = "182")
    private Long totalArticles;

    @Schema(description = "标签总数量", example = "95")
    private Long totalTags;

    @Schema(description = "收藏站点总数量", example = "156")
    private Long totalSites;

    @Schema(description = "笔记总数量", example = "42")
    private Long totalNotes;

    @Schema(description = "代码片段总数量", example = "128")
    private Long totalCodeSnippets;

    @Schema(description = "草稿数量", example = "15")
    private Integer draftCount;

    @Schema(description = "文章数量变化", example = "+10%")
    private String articleChange;

    @Schema(description = "标签数量变化", example = "-12%")
    private String tagChange;

    @Schema(description = "站点数量变化", example = "+30%")
    private String siteChange;

    @Schema(description = "访问量变化", example = "+20%")
    private String visitChange;
}