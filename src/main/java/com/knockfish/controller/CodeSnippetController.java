package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.annotation.PublicApi;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.code_snippet.CodeSnippetCreateDTO;
import com.knockfish.dto.code_snippet.CodeSnippetUpdateDTO;
import com.knockfish.service.CodeSnippetService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.code_snippet.CodeSnippetDetailVO;
import com.knockfish.vo.code_snippet.CodeSnippetVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code-snippet")
@Tag(name = "代码片段管理", description = "代码片段相关接口")
public class CodeSnippetController {
    private final CodeSnippetService codeSnippetService;

    @PublicApi
    @GetMapping("/list/{categoryId}")
    @Operation(summary = "获取分类下的代码片段列表", description = "根据分类ID获取代码片段列表")
    @Log("获取代码片段列表")
    public Result<PageResultVO<CodeSnippetVO>> getCodeSnippetListByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        PageInfo<CodeSnippetVO> pageInfo = codeSnippetService.getCodeSnippetListByCategoryId(categoryId, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(pageInfo));
    }

    @PublicApi
    @GetMapping("/{id}")
    @Operation(summary = "获取代码片段详情", description = "根据ID获取代码片段详情")
    @Log("获取代码片段详情")
    public Result<CodeSnippetDetailVO> getCodeSnippetById(
            @Parameter(description = "代码片段ID") @PathVariable Long id) {
        return Result.success(codeSnippetService.getCodeSnippetById(id));
    }

    @PostMapping
    @Operation(summary = "新增代码片段", description = "创建新的代码片段")
    @RequiresPermission("blog:code-snippet:add")
    @Log("新增代码片段")
    public Result<Void> createCodeSnippet(@Parameter(description = "代码片段创建信息") @Valid @RequestBody CodeSnippetCreateDTO createDTO) {
        codeSnippetService.createCodeSnippet(createDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新代码片段", description = "更新代码片段信息")
    @RequiresPermission("blog:code-snippet:edit")
    @Log("更新代码片段")
    public Result<Void> updateCodeSnippet(@Parameter(description = "代码片段更新信息") @Valid @RequestBody CodeSnippetUpdateDTO updateDTO) {
        codeSnippetService.updateCodeSnippet(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除代码片段", description = "根据ID删除代码片段")
    @RequiresPermission("blog:code-snippet:delete")
    @Log("删除代码片段")
    public Result<Void> deleteCodeSnippet(@Parameter(description = "代码片段ID") @PathVariable Long id) {
        codeSnippetService.deleteCodeSnippet(id);
        return Result.success();
    }
}