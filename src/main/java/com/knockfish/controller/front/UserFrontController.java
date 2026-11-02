package com.knockfish.controller.open;

import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.service.UserService;
import com.knockfish.vo.user.UserFrontVO;
import com.knockfish.vo.user.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/user")
@Tag(name = "前台-用户", description = "博客前台用户信息接口（匿名可访问）")
public class UserFrontController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "根据用户名查询用户", description = "根据用户名获取用户信息")
    @Log("前台按用户名查询用户")
    public Result<UserVO> getUserByUsername(@Parameter(description = "用户名") @RequestParam String username) {
        return Result.success(userService.getUserByUsername(username));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询用户", description = "根据id获取前台展示用用户信息")
    @Log("前台按ID查询用户")
    public Result<UserFrontVO> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(userService.getUserFrontById(id));
    }
}
