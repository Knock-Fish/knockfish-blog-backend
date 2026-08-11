package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CodeCategory {
    private Long codeCategoryId;
    private String codeCategoryName;
    private int sort;
    private LocalDateTime createTime;
}
