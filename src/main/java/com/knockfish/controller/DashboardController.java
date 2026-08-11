package com.knockfish.controller;

import com.knockfish.annotation.Log;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.service.DashboardService;
import com.knockfish.vo.dashboard.ActivityVO;
import com.knockfish.vo.dashboard.ArticleLatestVO;
import com.knockfish.vo.dashboard.ArticleTrendVO;
import com.knockfish.vo.dashboard.DashboardOverviewVO;
import com.knockfish.vo.tag.TagWithArticleCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@Tag(name = "仪表盘", description = "仪表盘统计数据接口")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    @Operation(summary = "获取仪表盘概览", description = "获取核心统计卡片数据")
    @RequiresPermission("blog:dashboard:view")
    @Log("获取仪表盘概览")
    public Result<DashboardOverviewVO> getOverview() {
        return Result.success(dashboardService.getOverview());
    }

    @GetMapping("/article-trend")
    @Operation(summary = "获取文章发布趋势", description = "获取文章发布数量趋势数据")
    @RequiresPermission("blog:dashboard:view")
    @Log("获取文章发布趋势")
    public Result<ArticleTrendVO> getArticleTrend(
            @Parameter(description = "时间周期：weekly/monthly/yearly") 
            @RequestParam(defaultValue = "monthly") String period,
            @Parameter(description = "年份") 
            @RequestParam(required = false) Integer year) {
        return Result.success(dashboardService.getArticleTrend(period, year));
    }

    @GetMapping("/latest-articles")
    @Operation(summary = "获取最近文章", description = "获取最近发布的文章列表")
    @RequiresPermission("blog:dashboard:view")
    @Log("获取最近文章")
    public Result<List<ArticleLatestVO>> getLatestArticles(
            @Parameter(description = "数量限制") 
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(dashboardService.getLatestArticles(limit));
    }

    @GetMapping("/tag-cloud")
    @Operation(summary = "获取标签云", description = "获取标签及文章数量统计")
    @RequiresPermission("blog:dashboard:view")
    @Log("获取标签云")
    public Result<List<TagWithArticleCountVO>> getTagCloud() {
        return Result.success(dashboardService.getTagCloud());
    }

    @GetMapping("/category-stats")
    @Operation(summary = "获取文章分类统计", description = "获取各分类下文章数量统计，用于饼图展示")
    @RequiresPermission("blog:dashboard:view")
    @Log("获取文章分类统计")
    public Result<List<TagWithArticleCountVO>> getCategoryStats() {
        return Result.success(dashboardService.getArticleCategoryStats());
    }

    @GetMapping("/activities")
    @Operation(summary = "获取最近动态", description = "获取用户最近的操作动态列表")
    @RequiresPermission("blog:dashboard:view")
    @Log("获取最近动态")
    public Result<List<ActivityVO>> getActivities(
            @Parameter(description = "数量限制")
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(dashboardService.getActivities(limit));
    }
}