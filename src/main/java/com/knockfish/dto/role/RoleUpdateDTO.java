package com.knockfish.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "角色更新请求DTO")
public class RoleUpdateDTO {
    @Schema(description = "角色ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long roleId;
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;
    @Schema(description = "角色描述", example = "系统管理员角色")
    private String description;
}
