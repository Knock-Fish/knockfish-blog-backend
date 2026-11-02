package com.knockfish.controller.admin;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/admin/code-snippet")
@Tag(name = "代码片段管理", description = "代码片段相关接口")
public class CodeSnippetAdminController {
    private final CodeSnippetService codeSnippetService;

    @PostMapping
    @Operation(summary = "新增代码片段", description = "创建新的代码片段")
    @PreAuthorize("hasAuthority('PERMISSION_blog:code-snippet:add')")
    @Log("新增代码片段")
    public Result<Void> createCodeSnippet(@Parameter(description = "代码片段创建信息") @Valid @RequestBody CodeSnippetCreateDTO createDTO) {
        codeSnippetService.createCodeSnippet(createDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新代码片段", description = "更新代码片段信息")
    @PreAuthorize("hasAuthority('PERMISSION_blog:code-snippet:edit')")
    @Log("更新代码片段")
    public Result<Void> updateCodeSnippet(@Parameter(description = "代码片段更新信息") @Valid @RequestBody CodeSnippetUpdateDTO updateDTO) {
        codeSnippetService.updateCodeSnippet(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除代码片段", description = "根据ID删除代码片段")
    @PreAuthorize("hasAuthority('PERMISSION_blog:code-snippet:delete')")
    @Log("删除代码片段")
    public Result<Void> deleteCodeSnippet(@Parameter(description = "代码片段ID") @PathVariable Long id) {
        codeSnippetService.deleteCodeSnippet(id);
        return Result.success();
    }
}