package com.knockfish.service.impl;

import com.knockfish.convert.GanttLinkConvert;
import com.knockfish.dto.gantt_link.GanttLinkCreateDTO;
import com.knockfish.entity.GanttLink;
import com.knockfish.repository.GanttLinkRepository;
import com.knockfish.security.CustomUserDetails;
import com.knockfish.service.GanttLinkService;
import com.knockfish.vo.gantt_link.GanttLinkVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GanttLinkServiceImpl implements GanttLinkService {

    private final GanttLinkRepository ganttLinkRepository;
    private final GanttLinkConvert ganttLinkConvert;

    @Override
    public List<GanttLinkVO> getLinkList() {
        Long userId = getCurrentUserId();
        List<GanttLink> linkList = ganttLinkRepository.selectByUserId(userId);
        return ganttLinkConvert.toVOList(linkList);
    }

    @Override
    public Long createLink(GanttLinkCreateDTO createDTO) {
        Long userId = getCurrentUserId();
        GanttLink entity = ganttLinkConvert.createToEntity(createDTO);
        entity.setUserId(userId);
        entity.setCreateTime(LocalDateTime.now());
        if (entity.getType() == 0 && createDTO.getType() != null) {
            entity.setType(createDTO.getType());
        }
        ganttLinkRepository.insert(entity);
        return entity.getLinkId();
    }

    @Override
    public void deleteLink(Long linkId) {
        Long userId = getCurrentUserId();
        // 简单校验：该 link 属于当前用户（可通过 join 查询；此处先查后判，避免误删其他用户数据）
        List<GanttLink> userLinks = ganttLinkRepository.selectByUserId(userId);
        boolean owned = userLinks.stream().anyMatch(l -> l.getLinkId().equals(linkId));
        if (!owned) {
            log.warn("删除连线失败: 连线不存在或不属于当前用户, linkId={}", linkId);
            return;
        }
        ganttLinkRepository.deleteByLinkId(linkId);
    }

    private Long getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
        }
        throw new IllegalStateException("用户未登录");
    }
}
