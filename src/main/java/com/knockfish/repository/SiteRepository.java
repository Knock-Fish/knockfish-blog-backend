package com.knockfish.repository;

import com.knockfish.dto.site.SiteQueryDTO;
import com.knockfish.entity.Site;
import com.knockfish.vo.site.SiteWithCategoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SiteRepository {
    List<SiteWithCategoryVO> selectSiteWithCategory(SiteQueryDTO query);
    void insert(Site site);
    void updateById(Site site);
    void deleteById(Long id);
    Long selectSiteCount();

    /**
     * Agent: 获取站点列表，可按分类ID过滤
     */
    List<SiteWithCategoryVO> selectSiteListForAgent(@Param("categoryId") Long categoryId);
}
