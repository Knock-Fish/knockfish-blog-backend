package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.annotation.PublicApi;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.link.LinkCreateDTO;
import com.knockfish.dto.link.LinkQueryDTO;
import com.knockfish.dto.link.LinkUpdateDTO;
import com.knockfish.service.LinkService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.link.LinkVO;

import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/link")
@Tag(name = "友链管理", description = "友链相关接口")
public class LinkController {
    private final LinkService linkService;

    @GetMapping("/page")
    @Operation(summary = "分页查询友链列表", description = "根据条件分页查询友链")
    @RequiresPermission("blog:link:manage")
    @Log("查询友链列表")
    public Result<PageResultVO<LinkVO>> getLinkListVO(
            @Parameter(description = "查询条件") LinkQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<LinkVO> listVO = linkService.getLinks(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }

    @GetMapping("/list")
    @Operation(summary = "获取友链列表", description = "获取所有友链列表（公开接口）")
    @PublicApi
    public Result<List<LinkVO>> getLinkList() {
        List<LinkVO> linkVOList = linkService.getLinkList();
        return Result.success(linkVOList);
    }

    @PostMapping
    @Operation(summary = "新增友链", description = "创建新的友链")
    @RequiresPermission("blog:link:add")
    @Log(value = "新增友链", recordResult = true)
    public Result<Long> createLink(@Parameter(description = "友链创建信息") @Valid @RequestBody LinkCreateDTO linkCreateDTO) {
        return Result.success(linkService.createLink(linkCreateDTO));
    }

    @PutMapping
    @Operation(summary = "更新友链", description = "更新友链信息")
    @RequiresPermission("blog:link:edit")
    @Log("更新友链")
    public Result<Void> updateLink(@Parameter(description = "友链更新信息") @Valid @RequestBody LinkUpdateDTO linkUpdateDTO) {
        linkService.updateLink(linkUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除友链", description = "根据ID删除友链")
    @RequiresPermission("blog:link:delete")
    @Log("删除友链")
    public Result<Void> deleteLink(@Parameter(description = "友链ID") @PathVariable Long id) {
        linkService.deleteLink(id);
        return Result.success();
    }
}
