package com.knockfish.vo.article;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "文章列表VO")
public class ArticleListVO {
    @Schema(description = "文章ID", example = "1")
    private Long articleId;
    @Schema(description = "文章标题", example = "Spring Boot入门教程")
    private String title;
    @Schema(description = "封面图片URL", example = "https://example.com/cover.jpg")
    private String cover;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "发布时间")
    private LocalDateTime publishTime;
}
