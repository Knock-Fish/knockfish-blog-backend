package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.link.LinkCreateDTO;
import com.knockfish.dto.link.LinkQueryDTO;
import com.knockfish.dto.link.LinkUpdateDTO;
import com.knockfish.vo.link.LinkVO;

import java.util.List;


public interface LinkService {
    PageInfo<LinkVO> getLinks(LinkQueryDTO query, Integer pageNum, Integer pageSize);
    List<LinkVO> getLinkList();
    Long createLink(LinkCreateDTO linkCreateDTO);
    void updateLink(LinkUpdateDTO linkUpdateDTO);
    void deleteLink(Long id);
}
