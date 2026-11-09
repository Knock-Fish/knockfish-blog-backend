package com.knockfish.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

/**
 * 相册令牌配置。
 *
 * <p>令牌是无状态 HMAC 签名，不依赖 Redis；密钥为空时生成随机密钥（重启后旧令牌全部失效）。
 * 生产环境请通过 {@code GALLERY_TOKEN_SECRET} 注入固定值，否则每次重启都要重新输密码。
 */
@Slf4j
@Getter
@Component
public class GalleryTokenConfig {

    @Value("${gallery.token-secret:}")
    private String tokenSecret;

    @Value("${gallery.token-ttl-minutes:30}")
    private long tokenTtlMinutes;

    private byte[] key;

    @PostConstruct
    public void init() {
        if (tokenSecret == null || tokenSecret.isBlank()) {
            key = new byte[32];
            new SecureRandom().nextBytes(key);
            log.warn("gallery.token-secret 未配置，已生成临时密钥：后端重启后所有相册令牌失效。"
                    + "生产请配置环境变量 GALLERY_TOKEN_SECRET");
        } else {
            key = tokenSecret.getBytes(StandardCharsets.UTF_8);
        }
        log.info("相册令牌有效期 {} 分钟", tokenTtlMinutes);
    }

    public long getTtlSeconds() {
        return Math.max(1, tokenTtlMinutes) * 60;
    }
}
