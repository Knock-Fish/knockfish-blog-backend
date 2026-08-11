package com.knockfish.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "分类更新请求DTO")
public class CategoryUpdateDTO {
    @Schema(description = "分类ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long categoryId;
    @Schema(description = "分类名称", example = "技术文章", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryName;
}
