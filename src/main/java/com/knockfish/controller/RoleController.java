package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.role.RoleCreateDTO;
import com.knockfish.dto.role.RolePermissionUpdateDTO;
import com.knockfish.dto.role.RoleQueryDTO;
import com.knockfish.dto.role.RoleUpdateDTO;
import com.knockfish.service.RoleService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.role.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role")
@Tag(name = "角色管理", description = "角色相关接口")
public class RoleController {
    private final RoleService roleService;


    @GetMapping("/with-permission")
    @Operation(summary = "获取角色列表（含权限）", description = "分页查询角色列表，包含角色拥有的权限信息")
    public Result<PageResultVO<RoleVO>> getRoleWithPermissions(
            @Parameter(description = "查询条件") RoleQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<RoleVO> listVO = roleService.getRoles(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }

    @PostMapping
    @Operation(summary = "新增角色", description = "创建新的角色")
    @RequiresPermission("blog:role:add")
    public Result<Void> createRole(@Parameter(description = "角色创建信息") @Valid @RequestBody RoleCreateDTO roleCreateDTO){
        roleService.createRole(roleCreateDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新角色", description = "更新角色信息")
    @RequiresPermission("blog:role:edit")
    public Result<Void> updateRole(@Parameter(description = "角色更新信息") @Valid @RequestBody RoleUpdateDTO roleUpdateDTO){
        roleService.updateRole(roleUpdateDTO);
        return Result.success();
    }

    @PutMapping("/permission")
    @Operation(summary = "更新角色权限", description = "更新角色的权限配置")
    @RequiresPermission("blog:permission:assign")
    public Result<Void> updateRolePermissions(@Parameter(description = "角色权限更新信息") @Valid @RequestBody RolePermissionUpdateDTO rolePermissionUpdateDTO){
        roleService.updateRolePermissions(rolePermissionUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    @RequiresPermission("blog:role:delete")
    public Result<Void> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id){
        roleService.deleteRole(id);
        return Result.success();
    }
}
