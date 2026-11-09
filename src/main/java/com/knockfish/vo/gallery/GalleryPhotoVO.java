package com.knockfish.vo.gallery;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图库照片出参。三个 URL 由 Service 用 cdnDomain + key 拼好，前端直接用。
 */
@Data
@Schema(description = "图库照片")
public class GalleryPhotoVO {
    @Schema(description = "照片ID")
    private Long photoId;

    @Schema(description = "原始文件名")
    private String fileName;

    @Schema(description = "展示标题")
    private String title;

    @Schema(description = "缩略图地址（WebP，宽 640）")
    private String thumbUrl;

    @Schema(description = "预览图地址（长边 1600），小图与 originalUrl 相同")
    private String previewUrl;

    @Schema(description = "原图地址")
    private String originalUrl;

    @Schema(description = "原图宽")
    private Integer width;

    @Schema(description = "原图高")
    private Integer height;

    @Schema(description = "原图字节数")
    private Long fileSize;

    @Schema(description = "原图 MIME")
    private String mimeType;

    @Schema(description = "是否敏感：0 普通 / 1 敏感（前台列表模糊）")
    private Integer nsfw;

    @Schema(description = "所属相册ID，空表示未归册")
    private Long albumId;

    @Schema(description = "上传人ID")
    private Long uploaderId;

    @Schema(description = "上传时间")
    private LocalDateTime createTime;
}
