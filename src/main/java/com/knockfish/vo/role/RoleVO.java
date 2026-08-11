package com.knockfish.vo.role;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Schema(description = "角色信息VO")
public class RoleVO {
    @Schema(description = "角色ID", example = "1")
    private Long roleId;
    @Schema(description = "角色名称", example = "管理员")
    private String roleName;
    @Schema(description = "角色描述", example = "系统管理员角色")
    private String description;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
