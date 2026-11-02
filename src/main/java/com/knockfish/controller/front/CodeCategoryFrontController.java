package com.knockfish.controller.open;

import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.service.CodeCategoryService;
import com.knockfish.vo.code_category.CodeCategoryMenuVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/code-category")
@Tag(name = "前台-代码分类", description = "博客前台代码分类接口（匿名可访问）")
public class CodeCategoryFrontController {
    private final CodeCategoryService codeCategoryService;

    @GetMapping("/menu")
    @Operation(summary = "获取代码分类菜单", description = "获取代码分类菜单列表")
    @Log("前台获取代码分类菜单")
    public Result<List<CodeCategoryMenuVO>> getMenuList() {
        return Result.success(codeCategoryService.getMenuList());
    }
}
