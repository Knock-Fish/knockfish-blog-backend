package com.knockfish.controller;

import com.knockfish.annotation.Log;
import com.knockfish.common.Result;
import com.knockfish.dto.auth.AuthLoginDTO;
import com.knockfish.service.AuthService;
import com.knockfish.vo.auth.AuthLoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户认证相关接口")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录获取token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功",
                    content = @Content(schema = @Schema(implementation = AuthLoginVO.class))),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @Log(value = "用户登录", recordResult = true)
    public Result<AuthLoginVO> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "登录信息", required = true) @RequestBody AuthLoginDTO authLoginDTO) {
        return Result.success(authService.login(authLoginDTO));
    }
}
