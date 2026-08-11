package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Tag {
    private Long tagId;
    private String tagName;
    private String color;
    private LocalDateTime createTime;
}
