package com.knockfish.convert;

import com.knockfish.dto.gantt_link.GanttLinkCreateDTO;
import com.knockfish.entity.GanttLink;
import com.knockfish.vo.gantt_link.GanttLinkVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GanttLinkConvert {

    // ==================== DTO -> Entity ====================
    GanttLink createToEntity(GanttLinkCreateDTO createDTO);

    // ==================== Entity -> VO ====================
    @Mapping(source = "linkId", target = "link_id")
    @Mapping(source = "createTime", target = "create_time")
    GanttLinkVO toVO(GanttLink link);

    @Mapping(source = "linkId", target = "link_id")
    @Mapping(source = "createTime", target = "create_time")
    List<GanttLinkVO> toVOList(List<GanttLink> linkList);
}
