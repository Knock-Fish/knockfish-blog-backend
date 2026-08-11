package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.site.SiteCreateDTO;
import com.knockfish.dto.site.SiteQueryDTO;
import com.knockfish.dto.site.SiteUpdateDTO;
import com.knockfish.vo.site.SiteWithCategoryVO;

public interface SiteService {
    PageInfo<SiteWithCategoryVO> getSitesWithCategory(SiteQueryDTO query, Integer pageNum, Integer pageSize);
    void createSite(SiteCreateDTO siteCreateDTO);
    void updateSite(SiteUpdateDTO siteUpdateDTO);
    void deleteSite(Long id);
}
