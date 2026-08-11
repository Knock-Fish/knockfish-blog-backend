package com.knockfish.convert;

import com.knockfish.dto.link.LinkCreateDTO;
import com.knockfish.dto.link.LinkUpdateDTO;
import com.knockfish.entity.Link;
import com.knockfish.vo.link.LinkVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LinkConvert {
    // ==================== DTO -> Entity ====================
    /**
     * 新增友链DTO 转 友链实体
     */
    Link createToEntity(LinkCreateDTO createDTO);
    /**
     * 更新友链DTO 转 友链实体
     */
    Link updateToEntity(LinkUpdateDTO updateDTO);
    // ==================== Entity -> VO ====================
    /**
     * 友链实体列表 转 列表VO
     */
    List<LinkVO> listToVOList(List<Link> linkList);
}
