package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.LinkConvert;
import com.knockfish.dto.link.LinkCreateDTO;
import com.knockfish.dto.link.LinkQueryDTO;
import com.knockfish.dto.link.LinkUpdateDTO;
import com.knockfish.entity.Link;
import com.knockfish.repository.LinkRepository;
import com.knockfish.service.LinkService;
import com.knockfish.vo.link.LinkVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkServiceImpl implements LinkService {
    private final LinkRepository linkRepository;
    private final LinkConvert linkConvert;
    @Override
    public PageInfo<LinkVO> getLinks(LinkQueryDTO query, Integer pageNum, Integer pageSize) {
        // 开始分页、暂存分页参数
        try (Page<LinkVO> page = PageHelper.startPage(pageNum, pageSize)) {
            // 查询数据库
            List<Link> linkListEntity = linkRepository.selectAll(query);
            // 将查到的数据进行二次组装，Link对象转换成LinkVO
            List<LinkVO> linkListVO = linkConvert.listToVOList(linkListEntity);
            // 组装分页参数
            PageInfo<LinkVO> pageInfo = PageInfo.of(page);
            // 设置结果列表
            pageInfo.setList(linkListVO);
            return pageInfo;
        }
    }

    @Override
    public List<LinkVO> getLinkList() {
        List<Link> linkListEntity = linkRepository.selectAllForFront();
        return linkConvert.listToVOList(linkListEntity);
    }
    @Override
    public Long createLink(LinkCreateDTO linkCreateDTO) {
        Link linkEntity = linkConvert.createToEntity(linkCreateDTO);
        linkEntity.setCreateTime(LocalDateTime.now());
        linkRepository.insert(linkEntity);
        return linkEntity.getLinkId();
    }
    @Override
    public void updateLink(LinkUpdateDTO linkUpdateDTO) {
        Link linkEntity = linkConvert.updateToEntity(linkUpdateDTO);
        linkRepository.updateById(linkEntity);
    }

    @Override
    public void deleteLink(Long id){
        linkRepository.deleteById(id);
    }
}
