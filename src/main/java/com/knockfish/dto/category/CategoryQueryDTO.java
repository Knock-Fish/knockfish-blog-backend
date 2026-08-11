package com.knockfish.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "分类查询请求DTO")
public class CategoryQueryDTO {
    @Schema(description = "分类名称", example = "技术文章")
    private String categoryName;
}
