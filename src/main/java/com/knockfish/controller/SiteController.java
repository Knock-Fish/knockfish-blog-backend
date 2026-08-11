package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.site.SiteCreateDTO;
import com.knockfish.dto.site.SiteQueryDTO;
import com.knockfish.dto.site.SiteUpdateDTO;
import com.knockfish.service.SiteService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.site.SiteWithCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/site")
@Tag(name = "站点管理", description = "站点相关接口")
public class SiteController {
    private final SiteService siteService;

    @GetMapping("/with-category")
    @Operation(summary = "获取站点列表（含分类）", description = "分页查询站点列表，包含关联的分类信息")
    @RequiresPermission("blog:site:manage")
    public Result<PageResultVO<SiteWithCategoryVO>> getSitesWithCategory(
            @Parameter(description = "查询条件") SiteQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<SiteWithCategoryVO> listVO = siteService.getSitesWithCategory(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }

    @PostMapping
    @Operation(summary = "新增站点", description = "创建新的站点")
    @RequiresPermission("blog:site:add")
    public Result<Void> createSite(@Parameter(description = "站点创建信息") @Valid @RequestBody SiteCreateDTO siteCreateDTO){
        siteService.createSite(siteCreateDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新站点", description = "更新站点信息")
    @RequiresPermission("blog:site:edit")
    public Result<Void> updateSite(@Parameter(description = "站点更新信息") @Valid @RequestBody SiteUpdateDTO siteUpdateDTO){
        siteService.updateSite(siteUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除站点", description = "根据ID删除站点")
    @RequiresPermission("blog:site:delete")
    public Result<Void> deleteSite(@Parameter(description = "站点ID") @PathVariable Long id){
        siteService.deleteSite(id);
        return Result.success();
    }
}
