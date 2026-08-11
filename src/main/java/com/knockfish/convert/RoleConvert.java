package com.knockfish.convert;

import com.knockfish.dto.role.RoleCreateDTO;
import com.knockfish.dto.role.RoleUpdateDTO;
import com.knockfish.entity.Role;
import com.knockfish.vo.role.RoleVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleConvert {
    // ==================== DTO -> Entity ====================
    Role createToEntity(RoleCreateDTO createDTO);
    Role updateToEntity(RoleUpdateDTO updateDTO);
    // ==================== Entity -> VO ===================='
    List<RoleVO> listToVOList(List<Role> roleList);
}
