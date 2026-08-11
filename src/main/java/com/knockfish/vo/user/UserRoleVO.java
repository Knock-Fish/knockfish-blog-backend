package com.knockfish.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "用户角色信息VO")
public class UserRoleVO {
    @Schema(description = "角色ID", example = "1")
    private Long roleId;
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;
}