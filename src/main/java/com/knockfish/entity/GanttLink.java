package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GanttLink {
    private Long linkId;
    private Long userId;
    private Long source;
    private Long target;
    private int type;
    private LocalDateTime createTime;
}
