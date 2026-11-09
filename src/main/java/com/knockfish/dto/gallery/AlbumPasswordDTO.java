package com.knockfish.dto.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设置 / 清除相册密码")
public class AlbumPasswordDTO {

    /**
     * 新密码；传空或 null 表示清除密码（相册变公开）。
     */
    @Schema(description = "新密码，留空表示清除密码变公开", example = "1234")
    private String password;
}
