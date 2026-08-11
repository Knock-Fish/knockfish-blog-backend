package com.knockfish.vo.article;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "文章列表（含标签）VO")
public class ArticleWithTagListVO extends ArticleVO {
    @Schema(description = "标签ID列表", example = "1,2,3")
    private String tagIds;
    @Schema(description = "标签名称列表", example = "Java,Spring,MySQL")
    private String tagNames;
    @Schema(description = "标签颜色列表", example = "#FF0000,#00FF00,#0000FF")
    private String tagColors;
    @Schema(description = "作者用户名", example = "admin")
    private String username;
}
