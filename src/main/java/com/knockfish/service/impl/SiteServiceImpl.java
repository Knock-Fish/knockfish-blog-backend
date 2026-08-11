package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.SiteConvert;
import com.knockfish.dto.site.SiteCreateDTO;
import com.knockfish.dto.site.SiteQueryDTO;
import com.knockfish.dto.site.SiteUpdateDTO;
import com.knockfish.entity.Site;
import com.knockfish.repository.SiteRepository;
import com.knockfish.service.SiteService;
import com.knockfish.vo.site.SiteWithCategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl implements SiteService {
    private final SiteRepository siteRepository;
    private final SiteConvert siteConvert;

    @Override
    public PageInfo<SiteWithCategoryVO> getSitesWithCategory(SiteQueryDTO query, Integer pageNum, Integer pageSize) {
        try(Page<Object> page = PageHelper.startPage(pageNum, pageSize)){
            return PageInfo.of(siteRepository.selectSiteWithCategory(query));
        }
    }
    @Override
    public void createSite(SiteCreateDTO siteCreateDTO){
        Site siteEntity = siteConvert.createToEntity(siteCreateDTO);
        siteEntity.setCreateTime(LocalDateTime.now());
        siteRepository.insert(siteEntity);
    }
    @Override
    public void updateSite(SiteUpdateDTO siteUpdateDTO){
        Site siteEntity = siteConvert.updateToEntity(siteUpdateDTO);
        siteRepository.updateById(siteEntity);
    }
    @Override
    public void deleteSite(Long id){
        siteRepository.deleteById(id);
    }
}
