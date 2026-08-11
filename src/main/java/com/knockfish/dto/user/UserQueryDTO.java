package com.knockfish.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "用户查询请求DTO")
public class UserQueryDTO {
    @Schema(description = "用户名", example = "admin")
    private String username;
    @Schema(description = "昵称", example = "张三")
    private String nickname;
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
}
