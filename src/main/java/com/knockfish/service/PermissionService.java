package com.knockfish.service;

import com.knockfish.dto.permission.PermissionCreateDTO;
import com.knockfish.dto.permission.PermissionQueryDTO;
import com.knockfish.dto.permission.PermissionUpdateDTO;
import com.knockfish.entity.Permission;
import com.knockfish.vo.permission.PermissionVO;

import java.util.List;

public interface PermissionService {

    /**
     * 根据用户ID查询所有权限
     */
    List<PermissionVO> getPermissionsByUserId(Long userId);

    /**
     * 根据角色ID查询所有权限
     */
    List<PermissionVO> getPermissionsByRoleId(Long roleId);
    /**
     * 根据角色ID查询所有权限
     */
    List<Long> getPermissionIdsByRoleId(Long roleId);
    /**
     * 查询所有权限
     */
    List<PermissionVO> getAllPermissions(PermissionQueryDTO query);

    /**
     * 根据ID查询权限
     */
    Permission getPermissionById(Long permissionId);

    /**
     * 新增权限
     */
    void createPermission(PermissionCreateDTO permissionCreateDTO);

    /**
     * 修改权限
     */
    void updatePermission(PermissionUpdateDTO permissionUpdateDTO);

    /**
     * 删除权限
     */
    void deletePermission(Long id);
}