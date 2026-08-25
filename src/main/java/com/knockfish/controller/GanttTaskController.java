package com.knockfish.controller;

import com.knockfish.annotation.Log;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.gantt_task.GanttTaskCreateDTO;
import com.knockfish.dto.gantt_task.GanttTaskUpdateDTO;
import com.knockfish.service.GanttTaskService;
import com.knockfish.vo.gantt_task.GanttTaskVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gantt/task")
@Tag(name = "甘特图任务管理", description = "甘特图任务相关接口")
public class GanttTaskController {

    private final GanttTaskService ganttTaskService;

    @GetMapping("/tree")
    @Operation(summary = "获取任务树", description = "获取当前登录用户的甘特图任务树（按parent_id组装层级）")
    @Log("获取甘特图任务树")
    public Result<List<GanttTaskVO>> getTaskTree() {
        return Result.success(ganttTaskService.getTaskTree());
    }

    @PostMapping
    @Operation(summary = "新增任务", description = "创建新的甘特图任务，支持插入到指定兄弟任务之后")
    @RequiresPermission("gantt:task:add")
    @Log("新增甘特图任务")
    public Result<Long> createTask(@Parameter(description = "任务创建信息") @Valid @RequestBody GanttTaskCreateDTO createDTO) {
        return Result.success(ganttTaskService.createTask(createDTO));
    }

    @PutMapping
    @Operation(summary = "更新任务", description = "更新甘特图任务信息")
    @RequiresPermission("gantt:task:edit")
    @Log("更新甘特图任务")
    public Result<Void> updateTask(@Parameter(description = "任务更新信息") @Valid @RequestBody GanttTaskUpdateDTO updateDTO) {
        ganttTaskService.updateTask(updateDTO);
        return Result.success();
    }

    @DeleteMapping("/{taskId}")
    @Operation(summary = "删除任务", description = "根据ID删除甘特图任务（递归删除子任务及关联的依赖连线）")
    @RequiresPermission("gantt:task:delete")
    @Log("删除甘特图任务")
    public Result<Void> deleteTask(@Parameter(description = "任务ID") @PathVariable Long taskId) {
        ganttTaskService.deleteTask(taskId);
        return Result.success();
    }
}
