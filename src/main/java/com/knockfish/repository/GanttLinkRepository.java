package com.knockfish.repository;

import com.knockfish.entity.GanttLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GanttLinkRepository {

    List<GanttLink> selectByUserId(@Param("userId") Long userId);

    void insert(GanttLink link);

    void deleteByLinkId(@Param("linkId") Long linkId);

    void deleteByTaskId(@Param("taskId") Long taskId);

    void deleteByUserId(@Param("userId") Long userId);
}
