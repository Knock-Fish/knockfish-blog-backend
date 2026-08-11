package com.knockfish.dto.permission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "权限查询请求DTO")
public class PermissionQueryDTO {
    @Schema(description = "权限ID", example = "1")
    private Long permissionId;
    @Schema(description = "权限名称", example = "用户管理")
    private String permissionName;
    @Schema(description = "权限编码", example = "user:manage")
    private String permissionCode;
}
