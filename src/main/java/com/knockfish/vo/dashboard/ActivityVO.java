package com.knockfish.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "最近动态VO")
public class ActivityVO {

    @Schema(description = "动态ID", example = "1")
    private Long id;

    @Schema(description = "动态类型：article_publish/article_update/tag_create/site_add", example = "article_publish")
    private String type;

    @Schema(description = "动态标题", example = "发布了文章")
    private String title;

    @Schema(description = "动态内容", example = "Spring Boot 性能优化实践")
    private String content;

    @Schema(description = "动态时间", example = "2024-01-15 14:30:00")
    private String time;

    @Schema(description = "相关链接", example = "/article/123")
    private String link;
}