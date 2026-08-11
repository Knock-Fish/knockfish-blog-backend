package com.knockfish.dto.permission;

import com.knockfish.enums.PermissionStatus;
import com.knockfish.enums.PermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "权限更新请求DTO")
public class PermissionUpdateDTO {
    @Schema(description = "权限ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long permissionId;
    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称", example = "用户管理", requiredMode = Schema.RequiredMode.REQUIRED)
    private String permissionName;
    @NotBlank(message = "权限类型不能为空")
    @Schema(description = "权限类型", example = "MENU", requiredMode = Schema.RequiredMode.REQUIRED)
    private PermissionType type;
    @Schema(description = "父级权限ID", example = "0")
    private int parentId;
    @Schema(description = "路由名称", example = "user")
    private String routeName;
    @Schema(description = "路由路径", example = "/user")
    private String path;
    @Schema(description = "图标", example = "user")
    private String icon;
    @Schema(description = "是否隐藏", example = "0")
    private int hidden;
    @Schema(description = "是否缓存", example = "0")
    private int keepAlive;
    @Schema(description = "组件路径", example = "user/index")
    private String component;
    @Schema(description = "排序号", example = "1")
    private int sortOrder;
    @Schema(description = "状态", example = "ACTIVE")
    private PermissionStatus status;
}
