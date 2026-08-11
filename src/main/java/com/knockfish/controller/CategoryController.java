package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.annotation.PublicApi;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.category.CategoryCreateDTO;
import com.knockfish.dto.category.CategoryQueryDTO;
import com.knockfish.dto.category.CategoryUpdateDTO;
import com.knockfish.service.CategoryService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.category.CategoryOptionVO;
import com.knockfish.vo.category.CategoryWithSiteCountVO;
import com.knockfish.vo.category.CategoryWithSiteListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/category")
@Tag(name = "分类管理", description = "文章分类相关接口")
public class CategoryController {
    private final CategoryService categoryService;

    @PublicApi
    @GetMapping("/with-site")
    @Operation(summary = "获取分类列表（含站点）", description = "获取所有分类及其关联的站点信息")
    @Log("获取分类列表(含站点)")
    public Result<List<CategoryWithSiteListVO>> getCategoryWithSiteList(){
        return Result.success(categoryService.getCategoryWithSiteList());
    }

    @GetMapping("/with-site-count")
    @Operation(summary = "分页获取分类列表（含统计）", description = "分页查询分类列表，包含文章数量统计")
    @RequiresPermission("blog:category:manage")
    @Log("查询分类列表(含统计)")
    public Result<PageResultVO<CategoryWithSiteCountVO>> getCategoryWithSiteCountList(
            @Parameter(description = "查询条件") CategoryQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<CategoryWithSiteCountVO> pageInfo = categoryService.getCategoryWithSiteCountList(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(pageInfo));
    }

    @GetMapping("/options")
    @Operation(summary = "获取分类选项", description = "获取分类下拉选项列表")
    @RequiresPermission("blog:category:manage")
    @Log("获取分类选项")
    public Result<List<CategoryOptionVO>> getCategoryOption(){
        return Result.success(categoryService.getCategoryOption());
    }

    @PostMapping
    @Operation(summary = "新增分类", description = "创建新的文章分类")
    @RequiresPermission("blog:category:add")
    @Log("新增分类")
    public Result<Void> createCategory(@Parameter(description = "分类创建信息") @Valid @RequestBody CategoryCreateDTO categoryCreateDTO){
        categoryService.createCategory(categoryCreateDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新分类", description = "更新分类信息")
    @RequiresPermission("blog:category:edit")
    @Log("更新分类")
    public Result<Void> updateCategory(@Parameter(description = "分类更新信息") @Valid @RequestBody CategoryUpdateDTO categoryUpdateDTO){
        categoryService.updateCategory(categoryUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "根据ID删除分类")
    @RequiresPermission("blog:category:delete")
    @Log("删除分类")
    public Result<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id){
        categoryService.deleteCategory(id);
        return Result.success();
    }
}
