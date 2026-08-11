package com.knockfish.dto.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "文章查询请求DTO")
public class ArticleQueryDTO {
    @Schema(description = "作者ID", example = "1")
    private Long userId;
    @Schema(description = "文章标题", example = "Spring Boot")
    private String title;
}
