package com.knockfish.vo.gallery;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 相册出参。
 *
 * <p>前台：{@code locked = true} 时不返回封面与照片数（否则“锁着”却漏一张图），
 * 前端画锁占位图即可；后台：两者都返回，方便管理。
 */
@Data
@Schema(description = "图库相册")
public class GalleryAlbumVO {

    @Schema(description = "相册ID")
    private Long albumId;

    @Schema(description = "相册名")
    private String name;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "封面地址；私密相册为空")
    private String coverUrl;

    @Schema(description = "封面照片ID；未设封面或私密相册为空")
    private Long coverPhotoId;

    @Schema(description = "照片数量；私密相册为空（前端显示锁）")
    private Long photoCount;

    @Schema(description = "是否加密：true 需要密码才能查看照片")
    private Boolean locked;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "0 正常 / 1 隐藏")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    /** 内部使用：封面图的 thumb_key，Service 拼成 URL 后清空，不对外暴露 */
    @JsonIgnore
    private String coverKey;
}
