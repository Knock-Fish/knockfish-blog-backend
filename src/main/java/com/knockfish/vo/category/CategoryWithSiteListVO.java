package com.knockfish.vo.category;

import com.knockfish.vo.site.SiteVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "分类（含站点列表）VO")
public class CategoryWithSiteListVO extends CategoryVO {
    @Schema(description = "关联站点列表")
    private List<SiteVO> sites;
}
