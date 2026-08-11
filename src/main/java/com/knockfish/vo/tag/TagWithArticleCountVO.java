package com.knockfish.vo.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "标签（含文章数量）VO")
public class TagWithArticleCountVO {
    @Schema(description = "标签ID", example = "1")
    private Long tagId;
    @Schema(description = "标签名称", example = "Java")
    private String tagName;
    @Schema(description = "标签颜色", example = "#FF0000")
    private String color;
    @Schema(description = "关联文章数量", example = "1")
    private Integer articleCount;
}
