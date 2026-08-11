package com.knockfish.repository;
import com.knockfish.dto.permission.PermissionQueryDTO;
import com.knockfish.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface PermissionRepository {

    // 根据用户ID查询所有权限
    List<Permission> selectPermissionsByUserId(@Param("userId") Long userId);

    // 根据角色ID查询所有权限
    List<Permission> selectPermissionsByRoleId(@Param("roleId") Long roleId);

    // 根据角色ID查询所有权限ID
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    // 查询所有权限
    List<Permission> selectAllPermissions(PermissionQueryDTO query);

    // 根据ID查权限
    Permission selectPermissionById(@Param("permissionId") Long permissionId);

    // 新增
    void insert(Permission permission);

    // 修改
    void updateById(Permission permission);
     
    // 删除
    void deleteById(@Param("id") Long id);
}