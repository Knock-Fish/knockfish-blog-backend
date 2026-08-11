package com.knockfish.controller;

import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.permission.PermissionCreateDTO;
import com.knockfish.dto.permission.PermissionQueryDTO;
import com.knockfish.dto.permission.PermissionUpdateDTO;
import com.knockfish.service.PermissionService;
import com.knockfish.vo.permission.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/permission")
@Tag(name = "权限管理", description = "权限相关接口")
public class PermissionController {
    private final PermissionService permissionService;

    @GetMapping
    @Operation(summary = "获取所有权限", description = "根据条件查询权限列表")
    @RequiresPermission("blog:permission:manage")
    public Result<Map<String, List<PermissionVO>>> getAllPermissions(@Parameter(description = "查询条件") PermissionQueryDTO query) {
        Map<String, List<PermissionVO>> result = new HashMap<>();
        result.put("list", permissionService.getAllPermissions(query));
        return Result.success(result);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID获取权限", description = "查询指定用户拥有的所有权限")
    @RequiresPermission("blog:permission:manage")
    public Result<Map<String, List<PermissionVO>>> getPermissionsByUserId(@Parameter(description = "用户ID") @PathVariable Long userId) {
        Map<String, List<PermissionVO>> result = new HashMap<>();
        result.put("list", permissionService.getPermissionsByUserId(userId));
        return Result.success(result);
    }

    @GetMapping("/role/{roleId}")
    @Operation(summary = "根据角色ID获取权限", description = "查询指定角色拥有的所有权限")
    @RequiresPermission("blog:permission:manage")
    public Result<Map<String, List<PermissionVO>>> getPermissionsByRoleId(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        Map<String, List<PermissionVO>> result = new HashMap<>();
        result.put("list", permissionService.getPermissionsByRoleId(roleId));
        return Result.success(result);
    }

    @GetMapping("/role/ids/{roleId}")
    @Operation(summary = "根据角色ID获取权限ID列表", description = "查询指定角色拥有的所有权限ID")
    public Result<Map<String, List<Long>>> getPermissionIdsByRoleId(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        Map<String, List<Long>> result = new HashMap<>();
        result.put("list", permissionService.getPermissionIdsByRoleId(roleId));
        return Result.success(result);
    }

    @PostMapping
    @Operation(summary = "新增权限", description = "创建新的权限")
    @RequiresPermission("blog:permission:add")
    public Result<Void> createPermission(@Parameter(description = "权限创建信息") @Valid @RequestBody PermissionCreateDTO permissionCreateDTO) {
        permissionService.createPermission(permissionCreateDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新权限", description = "更新权限信息")
    @RequiresPermission("blog:permission:edit")
    public Result<Void> updatePermission(@Parameter(description = "权限更新信息") @Valid @RequestBody PermissionUpdateDTO permissionUpdateDTO) {
        permissionService.updatePermission(permissionUpdateDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "根据ID删除权限")
    @RequiresPermission("blog:permission:delete")
    public Result<Void> deletePermission(@Parameter(description = "权限ID") @PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }
}
