package com.knockfish.entity;

import com.knockfish.enums.TaskStatus;
import com.knockfish.enums.TaskType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GanttTask {
    private Long taskId;
    private Long userId;
    private String text;
    private LocalDateTime start;
    private LocalDateTime end;
    private double progress;
    private TaskType type;
    private TaskStatus status;
    private String owner;
    private String description;
    private Long parentId;
    private Integer open;
    private Integer sortOrder;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
