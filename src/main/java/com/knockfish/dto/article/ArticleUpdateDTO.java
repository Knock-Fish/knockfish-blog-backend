package com.knockfish.dto.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.knockfish.enums.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "文章更新请求DTO")
public class ArticleUpdateDTO {
    @Schema(description = "文章ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long articleId;
    @Schema(description = "文章标题", example = "Spring Boot入门教程")
    private String title;
    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String cover;
    @Schema(description = "文章简介", example = "本文介绍Spring Boot的基本使用")
    private String description;
    @Schema(description = "文章内容", example = "# Hello World\\n\\n这是一篇测试文章")
    private String content;
    @Schema(description = "文章状态", example = "PUBLISHED")
    private ArticleStatus status;
    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tags;
}
