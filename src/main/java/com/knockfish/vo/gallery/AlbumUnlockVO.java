package com.knockfish.vo.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "相册解锁结果")
public class AlbumUnlockVO {

    @Schema(description = "访问令牌，之后请求该相册的照片时放在 X-Album-Token 头里")
    private String token;

    @Schema(description = "有效期（秒）")
    private Long expiresIn;
}
