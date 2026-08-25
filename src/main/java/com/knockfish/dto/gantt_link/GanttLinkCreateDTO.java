package com.knockfish.dto.gantt_link;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "甘特图依赖连线创建DTO")
public class GanttLinkCreateDTO {
    @Schema(description = "源任务ID", example = "1")
    private Long source;
    @Schema(description = "目标任务ID", example = "2")
    private Long target;
    @Schema(description = "连线类型(0=完成-开始)", example = "0")
    private Integer type;
}
