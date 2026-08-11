package com.knockfish.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "分类选项VO")
public class CategoryOptionVO {
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
    @Schema(description = "分类名称", example = "技术文章")
    private String categoryName;
}
