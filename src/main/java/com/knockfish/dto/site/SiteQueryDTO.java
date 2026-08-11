package com.knockfish.dto.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "站点查询请求DTO")
public class SiteQueryDTO {
    @Schema(description = "站点名称", example = "GitHub")
    private String siteName;
}
