package com.knockfish.controller;

import com.github.pagehelper.PageInfo;
import com.knockfish.annotation.Log;
import com.knockfish.annotation.PublicApi;
import com.knockfish.annotation.RequiresPermission;
import com.knockfish.common.Result;
import com.knockfish.dto.user.UserCreateDTO;
import com.knockfish.dto.user.UserQueryDTO;
import com.knockfish.dto.user.UserRoleUpdateDTO;
import com.knockfish.dto.user.UserUpdateDTO;
import com.knockfish.dto.user.UserUpdatePwdDTO;
import com.knockfish.exception.CustomException;
import com.knockfish.security.CustomUserDetails;
import com.knockfish.service.UserService;
import com.knockfish.utils.PageConvertUtil;
import com.knockfish.vo.PageResultVO;
import com.knockfish.vo.user.UserFrontVO;
import com.knockfish.vo.user.UserRoleVO;
import com.knockfish.vo.user.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "用户管理", description = "用户相关接口")
public class UserController {
    private final UserService userService;

    @GetMapping("/page")
    @Operation(summary = "分页查询用户列表", description = "根据条件分页查询用户")
    @RequiresPermission("blog:user:manage")
    @Log("查询用户列表")
    public Result<PageResultVO<UserVO>> getUserList(
            @Parameter(description = "查询条件") UserQueryDTO query,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize){
        PageInfo<UserVO> listVO = userService.getUserList(query, pageNum, pageSize);
        return Result.success(PageConvertUtil.convert(listVO));
    }

    @PublicApi
    @GetMapping
    @Operation(summary = "根据用户名查询用户", description = "根据用户名获取用户信息")
    @Log("查询用户信息")
    public Result<UserVO> getUserByUsername(@Parameter(description = "用户名") @RequestParam String username) {
        return Result.success(userService.getUserByUsername(username));
    }

    @PublicApi
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询用户", description = "根据id获取用户信息")
    @Log("查询用户信息")
    public Result<UserFrontVO> getUserById(@Parameter(description = "用户ID") @PathVariable Long id){
        return Result.success(userService.getUserFrontById(id));
    }

    @PostMapping
    @Operation(summary = "新增用户", description = "创建新用户")
    @RequiresPermission("blog:user:add")
    @Log("新增用户")
    public Result<Void> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        userService.createUser(userCreateDTO);
        return Result.success();
    }

    @PutMapping("/password")
    @Operation(summary = "更新用户密码", description = "修改当前用户的密码")
    @Log("更新用户密码")
    public Result<Void> updateUserPwd(
            @Parameter(description = "密码更新信息") @Validated @RequestBody UserUpdatePwdDTO userUpdatePwdDTO) {
        // 从SecurityContext获取当前用户ID
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new CustomException(401, "用户未登录");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        userUpdatePwdDTO.setUserId(userDetails.getUserId());
        userService.updateUserPwd(userUpdatePwdDTO);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "更新用户信息", description = "修改用户基本信息")
    @Log(value = "更新用户信息", recordResult = true)
    public Result<UserVO> updateUser(
            @Parameter(description = "用户更新信息") @Valid @RequestBody UserUpdateDTO userUpdateDTO) {

        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        userUpdateDTO.setUserId(userDetails.getUserId());
        return Result.success(userService.updateUser(userUpdateDTO));
    }

    @PutMapping("/role")
    @Operation(summary = "分配角色", description = "为用户分配角色")
    @RequiresPermission("blog:user:role")
    @Log("分配角色")
    public Result<Void> updateUserRoles(@Valid @RequestBody UserRoleUpdateDTO userRoleUpdateDTO) {
        userService.updateUserRoles(userRoleUpdateDTO);
        return Result.success();
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "查询用户角色", description = "根据用户ID查询用户拥有的角色")
    @RequiresPermission("blog:user:manage")
    public Result<List<UserRoleVO>> getUserRoles(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(userService.getUserRoles(id));
    }
}
