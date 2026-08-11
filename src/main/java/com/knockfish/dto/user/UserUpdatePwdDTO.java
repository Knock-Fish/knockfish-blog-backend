package com.knockfish.dto.user;

import com.knockfish.annotation.validation.PasswordMatch;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@PasswordMatch
@Schema(description = "用户密码更新请求DTO")
public class UserUpdatePwdDTO {
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码", example = "old123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String oldPassword;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20位之间")
    @Schema(description = "新密码", example = "new123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
    @NotBlank(message = "确认密码不能为空")
    @Size(min = 6, max = 20, message = "确认密码长度必须在6-20位之间")
    @Schema(description = "确认密码", example = "new123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
}
