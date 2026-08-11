package com.knockfish.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "分类创建请求DTO")
public class CategoryCreateDTO {
    @Schema(description = "分类名称", example = "技术文章", requiredMode = Schema.RequiredMode.REQUIRED)
    private String categoryName;
}
