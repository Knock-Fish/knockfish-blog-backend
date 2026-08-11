package com.knockfish.convert;


import com.knockfish.dto.tag.TagCreateDTO;
import com.knockfish.dto.tag.TagUpdateDTO;
import com.knockfish.entity.Tag;
import com.knockfish.vo.tag.TagVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagConvert {
    // ==================== DTO -> Entity ====================
    /**
     * 新增标签DTO 转 标签实体
     */
    Tag createToEntity(TagCreateDTO createDTO);
    /**
     * 更新标签DTO 转 标签实体
     */
    Tag updateToEntity(TagUpdateDTO updateDTO);
    // ==================== Entity -> VO ====================
    /**
     * 标签实体列表 转 列表VO
     */
    List<TagVO> listToVOList(List<Tag> tagList);
}
