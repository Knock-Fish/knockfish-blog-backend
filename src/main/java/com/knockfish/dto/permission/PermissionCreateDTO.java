package com.knockfish.dto.permission;

import com.knockfish.enums.PermissionStatus;
import com.knockfish.enums.PermissionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Schema(description = "权限创建请求DTO")
public class PermissionCreateDTO {
    @Schema(description = "权限ID")
    private Long permissionId;
    @NotBlank(message = "权限名称不能为空")
    @Schema(description = "权限名称", example = "用户管理", requiredMode = Schema.RequiredMode.REQUIRED)
    private String permissionName;
    @NotBlank(message = "权限编码不能为空")
    @Schema(description = "权限编码", example = "user:manage", requiredMode = Schema.RequiredMode.REQUIRED)
    private String permissionCode;
    @Schema(description = "权限类型", example = "menu", requiredMode = Schema.RequiredMode.REQUIRED)
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
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    @Schema(description = "状态", example = "enable")
    private PermissionStatus status;
}
