package com.knockfish.vo.gantt_link;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "甘特图依赖连线VO")
public class GanttLinkVO {
    @Schema(description = "连线ID")
    private Long link_id;
    @Schema(description = "源任务ID")
    private Long source;
    @Schema(description = "目标任务ID")
    private Long target;
    @Schema(description = "连线类型(0=完成-开始)")
    private Integer type;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_time;
}
