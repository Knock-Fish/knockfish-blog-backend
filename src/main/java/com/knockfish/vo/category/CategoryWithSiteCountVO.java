package com.knockfish.vo.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "分类（含统计）VO")
public class CategoryWithSiteCountVO extends CategoryVO{
    @Schema(description = "站点数量", example = "5")
    private int siteCount;
}
