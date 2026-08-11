package com.knockfish.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "角色查询请求DTO")
public class RoleQueryDTO {
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;
}
