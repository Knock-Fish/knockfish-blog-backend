package com.knockfish.service;

/**
 * 相册访问令牌：无状态 HMAC 签名，不落库、不依赖 Redis。
 *
 * <p>令牌格式与校验规则由实现类负责，调用方只关心「签发 / 验签 / 有效期」。
 */
public interface AlbumTokenService {

    /** 为相册签发令牌 */
    String issue(Long albumId, int passwordVer);

    /** 校验令牌是否对该相册（且密码未被改过、未过期）有效 */
    boolean verify(String token, Long albumId, int passwordVer);

    /** 令牌有效期（秒），用于返回给前端 */
    long getTtlSeconds();
}
