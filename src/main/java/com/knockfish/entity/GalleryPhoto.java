package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图库照片元数据。
 *
 * <p>图片二进制在 R2，本表只存 key 与元信息：
 * <ul>
 *   <li>{@code fileKey} 原图（保留上传时的原始编码，不转码）</li>
 *   <li>{@code thumbKey} 缩略图（WebP，宽 640），列表用水瀑流</li>
 *   <li>{@code previewKey} 预览图（WebP，长边 1600），大图查看用；小图不生成，回退原图</li>
 * </ul>
 */
@Data
public class GalleryPhoto {
    private Long photoId;
    /** 原图 R2 key */
    private String fileKey;
    /** 原始文件名 */
    private String fileName;
    /** 展示标题，空时前端回退文件名 */
    private String title;
    /** 原图 MIME */
    private String mimeType;
    /** 原图字节数 */
    private Long fileSize;
    /** 原图宽 */
    private Integer width;
    /** 原图高 */
    private Integer height;
    /** 缩略图 key */
    private String thumbKey;
    /** 预览图 key，可为空 */
    private String previewKey;
    /** 相册ID，暂未启用 */
    private Long albumId;
    /** 上传人 */
    private Long uploaderId;
    /** 0 正常 / 1 隐藏 / 2 回收站 */
    private Integer status;
    /** 0 普通 / 1 敏感（前台列表模糊，点开清晰） */
    private Integer nsfw;
    private LocalDateTime createTime;
}
