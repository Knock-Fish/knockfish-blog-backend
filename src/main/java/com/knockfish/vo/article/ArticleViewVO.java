package com.knockfish.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "文章视图VO")
public class ArticleViewVO extends ArticleVO {
    @Schema(description = "作者用户名", example = "admin")
    private String username;
}
