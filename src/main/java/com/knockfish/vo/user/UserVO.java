package com.knockfish.vo.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Schema(description = "用户信息VO")
public class UserVO {
    @Schema(description = "用户ID", example = "1")
    private Long userId;
    @Schema(description = "用户名", example = "admin")
    private String username;
    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;
    @Schema(description = "昵称", example = "管理员")
    private String nickname;
    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;
    @Schema(description = "个人描述", example = "这是我的个人简介")
    private String description;
    @Schema(description = "GitHub地址", example = "https://github.com/xxx")
    private String githubUrl;
    @Schema(description = "B站地址", example = "https://space.bilibili.com/xxx")
    private String bilibiliUrl;
    @Schema(description = "个人后台背景图", example = "https://example.com/background.jpg")
    private String background;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    @Schema(description = "角色列表")
    private List<UserRoleVO> roles;
}
