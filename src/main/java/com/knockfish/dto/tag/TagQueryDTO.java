package com.knockfish.dto.tag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "标签查询请求DTO")
public class TagQueryDTO {
    @Schema(description = "标签名称", example = "Java")
    private String tagName;
}
