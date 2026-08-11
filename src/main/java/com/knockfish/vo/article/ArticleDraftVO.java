package com.knockfish.vo.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "文章草稿 VO")
public class ArticleDraftVO {
    @Schema(description = "文章标题", example = "Spring Boot入门教程")
    private String title;
    @Schema(description = "文章ID", example = "1")
    private Long articleId;
    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String cover;
    @Schema(description = "文章描述", example = "这是一篇关于Spring Boot的入门教程")
    private String description;
    @Schema(description = "文章内容", example = "<p>文章内容...</p>")
    private String content;
    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;
    @Schema(description = "发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishTime;
}