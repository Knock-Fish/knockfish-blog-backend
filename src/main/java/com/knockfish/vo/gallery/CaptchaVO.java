package com.knockfish.vo.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "验证码")
public class CaptchaVO {

    @Schema(description = "验证码ID，解锁时回传")
    private String captchaId;

    @Schema(description = "png 图片的 data URL")
    private String image;
}
