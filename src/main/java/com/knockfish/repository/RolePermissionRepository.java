package com.knockfish.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RolePermissionRepository {
    void batchInsert(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
    void deleteByRoleId(@Param("roleId") Long roleId);
    void deleteByPermissionId(@Param("permissionId") Long permissionId);
}
