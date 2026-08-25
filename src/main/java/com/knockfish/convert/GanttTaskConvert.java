package com.knockfish.convert;

import com.knockfish.dto.gantt_task.GanttTaskCreateDTO;
import com.knockfish.dto.gantt_task.GanttTaskUpdateDTO;
import com.knockfish.entity.GanttTask;
import com.knockfish.vo.gantt_task.GanttTaskVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GanttTaskConvert {

    // ==================== DTO -> Entity ====================
    @Mapping(source = "parent_id", target = "parentId")
    GanttTask createToEntity(GanttTaskCreateDTO createDTO);

    @Mapping(source = "task_id", target = "taskId")
    @Mapping(source = "parent_id", target = "parentId")
    @Mapping(source = "sort_order", target = "sortOrder")
    GanttTask updateToEntity(GanttTaskUpdateDTO updateDTO);

    // ==================== Entity -> VO ====================
    @Mapping(source = "taskId", target = "task_id")
    @Mapping(source = "parentId", target = "parent_id")
    @Mapping(source = "sortOrder", target = "sort_order")
    @Mapping(source = "createTime", target = "create_time")
    @Mapping(source = "updateTime", target = "update_time")
    GanttTaskVO toVO(GanttTask task);

    @Mapping(source = "taskId", target = "task_id")
    @Mapping(source = "parentId", target = "parent_id")
    @Mapping(source = "sortOrder", target = "sort_order")
    @Mapping(source = "createTime", target = "create_time")
    @Mapping(source = "updateTime", target = "update_time")
    List<GanttTaskVO> toVOList(List<GanttTask> taskList);
}
