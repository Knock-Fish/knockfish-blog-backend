package com.knockfish.convert;

import com.knockfish.dto.site.SiteCreateDTO;
import com.knockfish.dto.site.SiteUpdateDTO;
import com.knockfish.entity.Site;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SiteConvert {
    // ==================== DTO -> Entity ====================
    /**
     * 新增网站DTO 转 网站实体
     */
    Site createToEntity(SiteCreateDTO createDTO);
    /**
     * 更新网站DTO 转 网站实体
     */
    Site updateToEntity(SiteUpdateDTO updateDTO);

    // ==================== Entity -> VO ====================

}
