package com.knockfish.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "用户角色更新DTO")
public class UserRoleUpdateDTO {
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "角色ID列表")
    private List<Long> roleIds;
}