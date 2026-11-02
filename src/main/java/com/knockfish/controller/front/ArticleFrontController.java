package com.knockfish.controller.open;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.dto.article.ArticleQueryDTO;
import com.knockfish.service.ArticleService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.article.ArticleArchiveVO;
import com.knockfish.vo.article.ArticleViewVO;
import com.knockfish.vo.article.ArticleWithTagListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 前台（博客站点）文章只读接口。
 * 路径统一挂在 /api/public/** 下，由 SecurityFilterChain 整段匿名放行，
 * 不再使用 @PublicApi 注解逐个标注。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/article")
@Tag(name = "前台-文章", description = "博客前台文章只读接口（匿名可访问）")
public class ArticleFrontController {
    private final ArticleService articleService;

    @GetMapping("/with-tag")
    @Operation(summary = "分页查询已发布文章（含标签）", description = "博客前台文章列表")
    @Log("前台查询文章列表(含标签)")
    public Result<PageResultVO<ArticleWithTagListVO>> getArticleWithTagList(
            @Parameter(description = "查询条件") ArticleQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<ArticleWithTagListVO> listVO = articleService.getArticleWithTags(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }

    @GetMapping("/archive")
    @Operation(summary = "获取文章归档列表", description = "按发布时间降序返回已发布文章的归档")
    @Log("前台获取文章归档")
    public Result<List<ArticleArchiveVO>> getArticleArchiveList() {
        return Result.success(articleService.getArticleArchiveList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情", description = "根据ID获取文章详情")
    @Log("前台获取文章详情")
    public Result<ArticleViewVO> getArticleById(@Parameter(description = "文章ID") @PathVariable Long id) {
        return Result.success(articleService.getArticleById(id));
    }
}
