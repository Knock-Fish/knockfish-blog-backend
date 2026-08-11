package com.knockfish.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.knockfish.convert.RoleConvert;
import com.knockfish.dto.role.RoleCreateDTO;
import com.knockfish.dto.role.RolePermissionUpdateDTO;
import com.knockfish.dto.role.RoleQueryDTO;
import com.knockfish.dto.role.RoleUpdateDTO;
import com.knockfish.entity.Role;
import com.knockfish.repository.RolePermissionRepository;
import com.knockfish.repository.RoleRepository;
import com.knockfish.service.RoleService;
import com.knockfish.vo.role.RoleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleConvert roleConvert;

    @Override
    public PageInfo<RoleVO> getRoles(RoleQueryDTO query, Integer pageNum, Integer pageSize) {
        try(Page<RoleVO> page = PageHelper.startPage(pageNum, pageSize)){
            List<Role> roleListEntity = roleRepository.selectAll(query);
            List<RoleVO> roleListVO = roleConvert.listToVOList(roleListEntity);
            PageInfo<RoleVO> pageInfo = PageInfo.of(page);
            pageInfo.setList(roleListVO);
            return pageInfo;
        }
    }

    @Override
    public void createRole(RoleCreateDTO roleCreateDTO) {
        Role roleEntity = roleConvert.createToEntity(roleCreateDTO);
        roleEntity.setCreateTime(LocalDateTime.now());
        roleRepository.insert(roleEntity);
    }

    @Override
    public void updateRole(RoleUpdateDTO roleUpdateDTO) {
        Role roleEntity = roleConvert.updateToEntity(roleUpdateDTO);
        roleRepository.updateById(roleEntity);
    }

    @Override
    public void updateRolePermissions(RolePermissionUpdateDTO rolePermissionUpdateDTO) {
        rolePermissionRepository.deleteByRoleId(rolePermissionUpdateDTO.getRoleId());
        rolePermissionRepository.batchInsert(
                rolePermissionUpdateDTO.getRoleId(),
                rolePermissionUpdateDTO.getPermissionIds()
        );
    }

    @Override
    public void deleteRole(Long id) {
        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.deleteById(id);
    }
}
