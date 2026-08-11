package com.knockfish.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "站点（含分类）VO")
public class SiteWithCategoryVO extends SiteVO {
    @Schema(description = "分类名称", example = "技术文章")
    private String categoryName;
}
