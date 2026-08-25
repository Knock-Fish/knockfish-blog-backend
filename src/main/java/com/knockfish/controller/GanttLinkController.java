package com.knockfish.controller;

import com.knockfish.annotation.Log;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.gantt_link.GanttLinkCreateDTO;
import com.knockfish.service.GanttLinkService;
import com.knockfish.vo.gantt_link.GanttLinkVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gantt/link")
@Tag(name = "甘特图依赖管理", description = "甘特图任务依赖连线相关接口")
public class GanttLinkController {

    private final GanttLinkService ganttLinkService;

    @GetMapping
    @Operation(summary = "获取依赖连线列表", description = "获取当前登录用户的所有任务依赖连线")
    @Log("获取甘特图依赖连线列表")
    public Result<List<GanttLinkVO>> getLinkList() {
        return Result.success(ganttLinkService.getLinkList());
    }

    @PostMapping
    @Operation(summary = "新增依赖连线", description = "创建任务之间的依赖连线（source->target）")
    @RequiresPermission("gantt:link:add")
    @Log("新增甘特图依赖连线")
    public Result<Long> createLink(@Parameter(description = "依赖连线创建信息") @Valid @RequestBody GanttLinkCreateDTO createDTO) {
        return Result.success(ganttLinkService.createLink(createDTO));
    }

    @DeleteMapping("/{linkId}")
    @Operation(summary = "删除依赖连线", description = "根据ID删除甘特图任务依赖连线")
    @RequiresPermission("gantt:link:delete")
    @Log("删除甘特图依赖连线")
    public Result<Void> deleteLink(@Parameter(description = "连线ID") @PathVariable Long linkId) {
        ganttLinkService.deleteLink(linkId);
        return Result.success();
    }
}
