package com.knockfish.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "标签更新请求DTO")
public class TagUpdateDTO {
    @NotBlank(message = "标签ID不能为空")
    @Schema(description = "标签ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long tagId;
    @NotBlank(message = "标签名称不能为空")
    @Schema(description = "标签名称", example = "Java", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tagName;
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "颜色格式必须为十六进制（如 #FF0000 或 #F00）")
    @Schema(description = "标签颜色（十六进制）", example = "#FF5733")
    private String color;
}
