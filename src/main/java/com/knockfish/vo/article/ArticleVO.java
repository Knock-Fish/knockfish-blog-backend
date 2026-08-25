package com.knockfish.vo.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.knockfish.enums.ArticleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "文章基础VO")
public class ArticleVO {
    @Schema(description = "文章ID", example = "1")
    private Long articleId;
    @Schema(description = "文章标题", example = "Spring Boot入门教程")
    private String title;
    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String cover;
    @Schema(description = "文章描述", example = "这是一篇关于Spring Boot的入门教程")
    private String description;
    @Schema(description = "文章内容", example = "<p>文章内容...</p>")
    private String content;
    @Schema(description = "文章状态", example = "publish")
    private ArticleStatus status;
    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    @Schema(description = "作者ID", example = "1")
    private Long userId;
}
