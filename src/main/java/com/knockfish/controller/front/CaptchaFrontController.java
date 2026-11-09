package com.knockfish.controller.front;

import com.knockfish.common.Result;
import com.knockfish.service.CaptchaService;
import com.knockfish.utils.IpUtil;
import com.knockfish.vo.gallery.CaptchaVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台验证码：目前只用于私密相册解锁。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/front/captcha")
@Tag(name = "前台-验证码", description = "私密相册解锁用")
public class CaptchaFrontController {

    private final CaptchaService captchaService;

    @GetMapping
    @Operation(summary = "获取验证码", description = "返回 captchaId 与 png 的 data URL，2 分钟内有效且只能用一次")
    public Result<CaptchaVO> create(HttpServletRequest request) {
        return Result.success(captchaService.create(IpUtil.resolve(request)));
    }
}
