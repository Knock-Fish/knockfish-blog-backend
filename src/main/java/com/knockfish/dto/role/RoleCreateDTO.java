package com.knockfish.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "角色创建请求DTO")
public class RoleCreateDTO {
    @Schema(description = "角色名称", example = "管理员", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;
    @Schema(description = "角色描述", example = "系统管理员角色")
    private String description;
}
