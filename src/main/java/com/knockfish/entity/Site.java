package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Site {
    private Long siteId;
    private String siteName;
    private String siteUrl;
    private String description;
    private String ico;
    private LocalDateTime createTime;
    private Long categoryId;
}
