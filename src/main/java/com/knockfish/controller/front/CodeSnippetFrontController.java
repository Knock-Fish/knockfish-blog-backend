package com.knockfish.controller.open;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.service.CodeSnippetService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.code_snippet.CodeSnippetDetailVO;
import com.knockfish.vo.code_snippet.CodeSnippetVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/code-snippet")
@Tag(name = "前台-代码片段", description = "博客前台代码片段接口（匿名可访问）")
public class CodeSnippetFrontController {
    private final CodeSnippetService codeSnippetService;

    @GetMapping("/list/{categoryId}")
    @Operation(summary = "获取分类下的代码片段列表", description = "根据分类ID分页获取代码片段")
    @Log("前台获取代码片段列表")
    public Result<PageResultVO<CodeSnippetVO>> getCodeSnippetListByCategoryId(
            @Parameter(description = "分类ID") @PathVariable Long categoryId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<CodeSnippetVO> pageInfo = codeSnippetService.getCodeSnippetListByCategoryId(categoryId, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(pageInfo));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取代码片段详情", description = "根据ID获取代码片段详情")
    @Log("前台获取代码片段详情")
    public Result<CodeSnippetDetailVO> getCodeSnippetById(@Parameter(description = "代码片段ID") @PathVariable Long id) {
        return Result.success(codeSnippetService.getCodeSnippetById(id));
    }
}
