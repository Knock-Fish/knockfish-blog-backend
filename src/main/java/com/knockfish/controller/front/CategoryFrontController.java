package com.knockfish.controller.open;

import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.service.CategoryService;
import com.knockfish.vo.category.CategoryWithSiteListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/category")
@Tag(name = "前台-分类", description = "博客前台分类接口（匿名可访问）")
public class CategoryFrontController {
    private final CategoryService categoryService;

    @GetMapping("/with-site")
    @Operation(summary = "获取分类列表（含站点）", description = "获取所有分类及其关联的站点信息")
    @Log("前台获取分类列表(含站点)")
    public Result<List<CategoryWithSiteListVO>> getCategoryWithSiteList() {
        return Result.success(categoryService.getCategoryWithSiteList());
    }
}
