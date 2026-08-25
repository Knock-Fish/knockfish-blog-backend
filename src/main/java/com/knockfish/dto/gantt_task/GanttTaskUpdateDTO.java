package com.knockfish.dto.gantt_task;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.knockfish.enums.TaskStatus;
import com.knockfish.enums.TaskType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "甘特图任务更新DTO")
public class GanttTaskUpdateDTO {
    @Schema(description = "任务ID", example = "1")
    private Long task_id;
    @Schema(description = "任务名称")
    private String text;
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    @Schema(description = "完成进度(0~1)")
    private Double progress;
    @Schema(description = "任务类型")
    private TaskType type;
    @Schema(description = "任务状态")
    private TaskStatus status;
    @Schema(description = "负责人")
    private String owner;
    @Schema(description = "任务描述")
    private String description;
    @Schema(description = "父任务ID")
    private Long parent_id;
    @Schema(description = "是否展开(0=收起 1=展开)")
    private Integer open;
    @Schema(description = "同级排序号")
    private Integer sort_order;
}
