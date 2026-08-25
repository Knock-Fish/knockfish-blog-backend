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
@Schema(description = "甘特图任务创建DTO")
public class GanttTaskCreateDTO {
    @Schema(description = "任务名称", example = "需求分析")
    private String text;
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    @Schema(description = "完成进度(0~1)", example = "0")
    private Double progress;
    @Schema(description = "任务类型", example = "task")
    private TaskType type;
    @Schema(description = "任务状态", example = "todo")
    private TaskStatus status;
    @Schema(description = "负责人", example = "张三")
    private String owner;
    @Schema(description = "任务描述")
    private String description;
    @Schema(description = "父任务ID(顶层为null)", example = "1")
    private Long parent_id;
    @Schema(description = "插入到该兄弟任务之后(null=追加到末尾)", example = "2")
    private Long insert_after_id;
}
