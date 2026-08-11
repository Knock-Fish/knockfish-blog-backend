package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.role.RoleCreateDTO;
import com.knockfish.dto.role.RolePermissionUpdateDTO;
import com.knockfish.dto.role.RoleQueryDTO;
import com.knockfish.dto.role.RoleUpdateDTO;
import com.knockfish.vo.role.RoleVO;

public interface RoleService {
    PageInfo<RoleVO> getRoles(RoleQueryDTO query, Integer pageNum, Integer pageSize);
    void createRole(RoleCreateDTO roleCreateDTO);
    void updateRole(RoleUpdateDTO roleUpdateDTO);
    void updateRolePermissions(RolePermissionUpdateDTO rolePermissionUpdateDTO);
    void deleteRole(Long id);
}
