package com.knockfish.repository;

import com.knockfish.dto.role.RoleQueryDTO;
import com.knockfish.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleRepository {
    List<Role> selectAll(RoleQueryDTO query);
    void insert(Role role);
    void updateById(Role role);
    void deleteById(Long id);
}
