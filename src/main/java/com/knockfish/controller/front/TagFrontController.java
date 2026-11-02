package com.knockfish.controller.open;

import com.github.pagehelper.PageInfo;
import com.knockfish.common.Result;
import com.knockfish.service.TagService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.article.ArticleVO;
import com.knockfish.vo.tag.TagWithArticleCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/tag")
@Tag(name = "前台-标签", description = "博客前台标签接口（匿名可访问）")
public class TagFrontController {
    private final TagService tagService;

    @GetMapping
    @Operation(summary = "分页查询标签（含文章数）", description = "博客前台标签列表")
    public Result<PageResultVO<TagWithArticleCountVO>> getTagWithArticleCount(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<TagWithArticleCountVO> listVO = tagService.getTagWithArticleCount(pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }

    @GetMapping("/{id}/articles")
    @Operation(summary = "根据标签ID查询文章列表", description = "查询指定标签关联的所有已发布文章")
    public Result<List<ArticleVO>> getArticlesByTagId(@Parameter(description = "标签ID") @PathVariable Long id) {
        return Result.success(tagService.getArticlesByTagId(id));
    }
}
