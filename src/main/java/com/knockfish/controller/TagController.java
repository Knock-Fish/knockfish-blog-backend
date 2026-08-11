package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.annotation.PublicApi;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.tag.TagCreateDTO;
import com.knockfish.dto.tag.TagQueryDTO;
import com.knockfish.dto.tag.TagUpdateDTO;
import com.knockfish.service.TagService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.article.ArticleVO;
import com.knockfish.vo.tag.TagVO;
import com.knockfish.vo.tag.TagWithArticleCountVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tag")
@Tag(name = "标签管理", description = "文章标签相关接口")
public class TagController {
    private final TagService tagService;

    @GetMapping("/page")
    @Operation(summary = "分页查询标签列表", description = "根据条件分页查询标签")
    @RequiresPermission("blog:tag:manage")
    @Log("查询标签列表")
    public Result<PageResultVO<TagVO>> getTagListVO(
            @Parameter(description = "查询条件") TagQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<TagVO> listVO = tagService.getTags(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }
    @PublicApi
    @GetMapping
    public Result<PageResultVO<TagWithArticleCountVO>> getTagWithArticleCount(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<TagWithArticleCountVO> listVO = tagService.getTagWithArticleCount(pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }
    @PublicApi
    @GetMapping("/{id}/articles")
    @Operation(summary = "根据标签ID查询文章列表", description = "查询指定标签关联的所有已发布文章")
    public Result<List<ArticleVO>> getArticlesByTagId(
            @Parameter(description = "标签ID") @PathVariable Long id) {
        List<ArticleVO> articles = tagService.getArticlesByTagId(id);
        return Result.success(articles);
    }

    @PostMapping
    @Operation(summary = "新增标签", description = "创建新的标签")
    @RequiresPermission("blog:tag:add")
    @Log(value = "新增标签", recordResult = true)
    public Result<Long> createTag(@Parameter(description = "标签创建信息") @Valid @RequestBody TagCreateDTO tagCreateDTO) {
        return Result.success(tagService.createTag(tagCreateDTO));
    }

    @PutMapping
    @Operation(summary = "更新标签", description = "更新标签信息")
    @RequiresPermission("blog:tag:edit")
    @Log("更新标签")
    public Result<Void> updateTag(@Parameter(description = "标签更新信息") @Valid @RequestBody TagUpdateDTO tagUpdateDTO) {
        tagService.updateTag(tagUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签", description = "根据ID删除标签")
    @RequiresPermission("blog:tag:delete")
    @Log("删除标签")
    public Result<Void> deleteTag(@Parameter(description = "标签ID") @PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success();
    }
}
