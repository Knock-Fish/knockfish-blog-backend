package com.knockfish.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "用户更新请求DTO")
public class UserUpdateDTO {
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;
    @Schema(description = "昵称", example = "张三")
    private String nickname;
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    @Schema(description = "文件id", example = "1")
    private Long fileId;
    @Schema(description = "个人描述", example = "这是我的个人简介")
    private String description;
    @Schema(description = "GitHub地址", example = "https://github.com/xxx")
    private String githubUrl;
    @Schema(description = "B站地址", example = "https://space.bilibili.com/xxx")
    private String bilibiliUrl;
    @Schema(description = "个人背景图", example = "https://example.com/bg.jpg")
    private String background;
}
