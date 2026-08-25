package com.knockfish.service;

import com.knockfish.dto.gantt_task.GanttTaskCreateDTO;
import com.knockfish.dto.gantt_task.GanttTaskUpdateDTO;
import com.knockfish.vo.gantt_task.GanttTaskVO;

import java.util.List;

public interface GanttTaskService {

    /**
     * 获取当前用户的甘特图任务树
     */
    List<GanttTaskVO> getTaskTree();

    /**
     * 新增任务（支持插入到指定兄弟任务之后）
     */
    Long createTask(GanttTaskCreateDTO createDTO);

    /**
     * 更新任务
     */
    void updateTask(GanttTaskUpdateDTO updateDTO);

    /**
     * 删除任务（递归删除子任务及相关依赖连线）
     */
    void deleteTask(Long taskId);
}
