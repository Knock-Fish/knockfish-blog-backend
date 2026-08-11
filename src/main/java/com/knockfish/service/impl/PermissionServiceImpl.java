package com.knockfish.service.impl;

import com.knockfish.convert.PermissionConvert;
import com.knockfish.dto.permission.PermissionCreateDTO;
import com.knockfish.dto.permission.PermissionQueryDTO;
import com.knockfish.dto.permission.PermissionUpdateDTO;
import com.knockfish.entity.Permission;
import com.knockfish.repository.PermissionRepository;
import com.knockfish.repository.RolePermissionRepository;
import com.knockfish.service.PermissionService;
import com.knockfish.vo.permission.PermissionVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionConvert permissionConvert;

    @Override
    public List<PermissionVO> getPermissionsByUserId(Long userId) {
        List<Permission> permissionListEntity = permissionRepository.selectPermissionsByUserId(userId);
        return permissionConvert.listToVOlist(permissionListEntity);
    }

    @Override
    public List<PermissionVO> getPermissionsByRoleId(Long roleId) {
        List<Permission> permissionListEntity = permissionRepository.selectPermissionsByRoleId(roleId);
        return permissionConvert.listToVOlist(permissionListEntity);
    }

    @Override
    public List<Long> getPermissionIdsByRoleId(Long roleId) {
        return permissionRepository.selectPermissionIdsByRoleId(roleId);
    }

    @Override
    public List<PermissionVO> getAllPermissions(PermissionQueryDTO query) {
        List<Permission> permissionListEntity = permissionRepository.selectAllPermissions(query);
        return permissionConvert.listToVOlist(permissionListEntity);
    }

    @Override
    public Permission getPermissionById(Long permissionId) {
        return null;
    }

    @Override
    public void createPermission(PermissionCreateDTO permissionCreateDTO) {
        Permission permissionEntity = permissionConvert.createToEntity(permissionCreateDTO);
        permissionRepository.insert(permissionEntity);
    }

    @Override
    public void updatePermission(PermissionUpdateDTO permissionUpdateDTO) {
        Permission permissionEntity = permissionConvert.updateToEntity(permissionUpdateDTO);
        permissionRepository.updateById(permissionEntity);
    }

    @Override
    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
        rolePermissionRepository.deleteByPermissionId(id);
    }
}
