package com.knockfish.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "最近文章VO")
public class ArticleLatestVO {

    @Schema(description = "文章ID", example = "123")
    private Long articleId;

    @Schema(description = "文章标题", example = "Spring Boot 性能优化实践")
    private String title;

    @Schema(description = "封面图片")
    private String cover;

    @Schema(description = "发布时间", example = "2024-01-15 14:30:00")
    private String publishTime;
}