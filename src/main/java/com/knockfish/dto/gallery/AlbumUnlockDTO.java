package com.knockfish.dto.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "相册解锁入参")
public class AlbumUnlockDTO {

    @Schema(description = "验证码ID")
    @NotBlank(message = "验证码不能为空")
    private String captchaId;

    @Schema(description = "用户输入的验证码")
    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @Schema(description = "相册密码")
    @NotBlank(message = "密码不能为空")
    private String password;
}
