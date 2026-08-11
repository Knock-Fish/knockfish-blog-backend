package com.knockfish.vo.category;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "分类信息VO")
public class CategoryVO {
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;
    @Schema(description = "分类名称", example = "技术文章")
    private String categoryName;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
