package com.knockfish.vo.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "文章发布趋势VO")
public class ArticleTrendVO {

    @Schema(description = "标签（月份）")
    private List<String> labels;

    @Schema(description = "数量值")
    private List<Integer> values;

    @Schema(description = "时间周期", example = "monthly")
    private String period;

    @Schema(description = "年份", example = "2024")
    private Integer year;
}