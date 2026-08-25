package com.knockfish.service;

import com.knockfish.dto.gantt_link.GanttLinkCreateDTO;
import com.knockfish.vo.gantt_link.GanttLinkVO;

import java.util.List;

public interface GanttLinkService {

    /**
     * 获取当前用户的所有依赖连线
     */
    List<GanttLinkVO> getLinkList();

    /**
     * 新增依赖连线
     */
    Long createLink(GanttLinkCreateDTO createDTO);

    /**
     * 删除依赖连线
     */
    void deleteLink(Long linkId);
}
