package com.knockfish.entity;

import com.knockfish.enums.ArticleStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Article {
    private Long articleId;
    private String title;
    private String cover;
    private String description;
    private String content;
    private ArticleStatus status;
    private LocalDateTime publishTime;
    private LocalDateTime updatedTime;
    private Long userId;
}
