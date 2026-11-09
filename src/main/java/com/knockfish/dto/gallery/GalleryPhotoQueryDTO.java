package com.knockfish.dto.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "图库照片查询条件")
public class GalleryPhotoQueryDTO {
    @Schema(description = "关键字，匹配标题或文件名")
    private String keyword;

    @Schema(description = "朝向：all / landscape / portrait / square", example = "all")
    private String orientation;

    @Schema(description = "状态：0 正常 / 1 隐藏 / 2 回收站；不传默认 0")
    private Integer status;

    @Schema(description = "相册ID；不传表示不按相册过滤")
    private Long albumId;

    @Schema(description = "只看公开相册的照片（前台用：私密相册的照片不能进入「全部」）")
    private Boolean excludeLockedAlbum;

    @Schema(description = "只看未归册的照片（后台「未归册」视图）")
    private Boolean onlyUnassigned;
}
