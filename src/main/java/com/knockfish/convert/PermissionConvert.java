package com.knockfish.convert;


import com.knockfish.dto.permission.PermissionCreateDTO;
import com.knockfish.dto.permission.PermissionUpdateDTO;
import com.knockfish.entity.Permission;
import com.knockfish.vo.permission.PermissionVO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionConvert {
    // DTO -> Entity
    Permission createToEntity(PermissionCreateDTO createDTO);
    Permission updateToEntity(PermissionUpdateDTO updateDTO);


    List<PermissionVO> listToVOlist(List<Permission> permissionList);
}
