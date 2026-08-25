package com.knockfish.repository;

import com.knockfish.entity.GanttTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GanttTaskRepository {

    List<GanttTask> selectByUserId(@Param("userId") Long userId);

    GanttTask selectByTaskId(@Param("taskId") Long taskId);

    void insert(GanttTask task);

    void updateById(GanttTask task);

    void deleteByTaskId(@Param("taskId") Long taskId);

    void deleteByParentId(@Param("parentId") Long parentId);

    void deleteByUserId(@Param("userId") Long userId);

    void updateSortOrder(@Param("taskId") Long taskId, @Param("sortOrder") Integer sortOrder);

    void shiftSortOrder(@Param("userId") Long userId,
                        @Param("parentId") Long parentId,
                        @Param("fromSortOrder") Integer fromSortOrder);
}
