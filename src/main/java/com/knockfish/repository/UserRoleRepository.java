package com.knockfish.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserRoleRepository {
    void batchInsert(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);
    void deleteByUserId(@Param("userId") Long userId);
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}