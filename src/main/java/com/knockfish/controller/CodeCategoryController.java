package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.annotation.PublicApi;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.code_category.CodeCategoryCreateDTO;
import com.knockfish.dto.code_category.CodeCategoryQueryDTO;
import com.knockfish.dto.code_category.CodeCategoryUpdateDTO;
import com.knockfish.service.CodeCategoryService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.code_category.CodeCategoryMenuVO;
import com.knockfish.vo.code_category.CodeCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code-category")
@Tag(name = "代码分类管理", description = "代码分类相关接口")
public class CodeCategoryController {
    private final CodeCategoryService codeCategoryService;

    @PublicApi
    @GetMapping("/menu")
    @Operation(summary = "获取代码分类菜单", description = "获取代码分类菜单列表")
    @Log("获取代码分类菜单")
    public Result<List<CodeCategoryMenuVO>> getMenuList() {
        return Result.success(codeCategoryService.getMenuList());
    }

    @GetMapping
    @Operation(summary = "分页获取代码分类列表", description = "分页查询代码分类列表")
    @RequiresPermission("blog:code-category:manage")
    @Log("查询代码分类列表")
    public Result<PageResultVO<CodeCategoryVO>> getCodeCategoryList(
            @Parameter(description = "查询条件") CodeCategoryQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<CodeCategoryVO> pageInfo = codeCategoryService.getCodeCategoryList(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(pageInfo));
    }

    @PostMapping
    @Operation(summary = "新增代码分类", description = "创建新的代码分类")
    @RequiresPermission("blog:code-category:add")
    @Log("新增代码分类")
    public Result<Void> createCodeCategory(@Parameter(description = "代码分类创建信息") @Valid @RequestBody CodeCategoryCreateDTO createDTO) {
        codeCategoryService.createCodeCategory(createDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新代码分类", description = "更新代码分类信息")
    @RequiresPermission("blog:code-category:edit")
    @Log("更新代码分类")
    public Result<Void> updateCodeCategory(@Parameter(description = "代码分类更新信息") @Valid @RequestBody CodeCategoryUpdateDTO updateDTO) {
        codeCategoryService.updateCodeCategory(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除代码分类", description = "根据ID删除代码分类")
    @RequiresPermission("blog:code-category:delete")
    @Log("删除代码分类")
    public Result<Void> deleteCodeCategory(@Parameter(description = "代码分类ID") @PathVariable Long id) {
        codeCategoryService.deleteCodeCategory(id);
        return Result.success();
    }
}