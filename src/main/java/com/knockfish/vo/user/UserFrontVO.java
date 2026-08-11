package com.knockfish.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "前端用户信息VO")
public class UserFrontVO {
    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "昵称", example = "管理员")
    private String nickname;

    @Schema(description = "邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "个人描述", example = "这是我的个人简介")
    private String description;

    @Schema(description = "GitHub地址", example = "https://github.com/xxx")
    private String githubUrl;

    @Schema(description = "B站地址", example = "https://space.bilibili.com/xxx")
    private String bilibiliUrl;

    @Schema(description = "文章总数", example = "12")
    private Integer articleCount;

    @Schema(description = "标签总数", example = "8")
    private Integer tagCount;

    @Schema(description = "集站总数", example = "5")
    private Integer siteCount;
}