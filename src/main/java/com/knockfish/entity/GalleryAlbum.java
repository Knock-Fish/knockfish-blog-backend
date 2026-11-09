package com.knockfish.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 图库相册。
 *
 * <p>{@code passwordHash} 非空即私密：前台不返回该相册的照片，必须先解锁拿到令牌。
 * {@code passwordVer} 在改密码时 +1，用来让之前签发的令牌全部失效。
 */
@Data
public class GalleryAlbum {
    private Long albumId;
    private String name;
    private String description;
    /** 封面照片，为空时取该相册第一张 */
    private Long coverPhotoId;
    /** BCrypt 哈希；null 表示公开 */
    private String passwordHash;
    /** 密码版本，改密码时 +1 */
    private Integer passwordVer;
    private Integer sortOrder;
    /** 0 正常 / 1 隐藏 */
    private Integer status;
    private LocalDateTime createTime;
}
