package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Category {
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createTime;
}
