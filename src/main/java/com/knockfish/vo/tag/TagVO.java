package com.knockfish.vo.tag;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "标签信息VO")
public class TagVO {
    @Schema(description = "标签ID", example = "1")
    private Long tagId;
    @Schema(description = "标签名称", example = "Java")
    private String tagName;
    @Schema(description = "标签颜色", example = "#FF0000")
    private String color;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
