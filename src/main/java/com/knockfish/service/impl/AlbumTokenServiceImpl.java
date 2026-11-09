package com.knockfish.service.impl;

import com.knockfish.config.GalleryTokenConfig;
import com.knockfish.service.AlbumTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

/**
 * 相册令牌实现：无状态 HMAC 签名，不落库、不依赖 Redis。
 *
 * <p>格式：{@code base64(payload) + "." + base64(hmac(payload))}，
 * payload 为 {@code albumId|passwordVer|exp}。改密码时 passwordVer +1，
 * 之前签发的令牌全部自动失效，不需要维护黑名单。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AlbumTokenServiceImpl implements AlbumTokenService {

    private static final String ALGORITHM = "HmacSHA256";
    private static final Base64.Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
    private static final Base64.Decoder DECODER = Base64.getUrlDecoder();

    private final GalleryTokenConfig config;

    @Override
    public String issue(Long albumId, int passwordVer) {
        long expireAt = System.currentTimeMillis() / 1000 + config.getTtlSeconds();
        String payload = albumId + "|" + passwordVer + "|" + expireAt;
        return ENCODER.encodeToString(payload.getBytes(StandardCharsets.UTF_8))
                + "." + ENCODER.encodeToString(sign(payload));
    }

    @Override
    public boolean verify(String token, Long albumId, int passwordVer) {
        if (token == null || token.isBlank() || albumId == null) {
            return false;
        }
        int dot = token.indexOf('.');
        if (dot <= 0 || dot == token.length() - 1) {
            return false;
        }
        try {
            String payload = new String(DECODER.decode(token.substring(0, dot)), StandardCharsets.UTF_8);
            byte[] signature = DECODER.decode(token.substring(dot + 1));
            // 先验签，再解析内容，避免被构造的 payload 误导
            if (!MessageDigest.isEqual(signature, sign(payload))) {
                return false;
            }
            String[] parts = payload.split("\\|");
            if (parts.length != 3) {
                return false;
            }
            long targetAlbum = Long.parseLong(parts[0]);
            int ver = Integer.parseInt(parts[1]);
            long expireAt = Long.parseLong(parts[2]);
            return targetAlbum == albumId
                    && ver == passwordVer
                    && expireAt > System.currentTimeMillis() / 1000;
        } catch (RuntimeException e) {
            return false;
        }
    }

    @Override
    public long getTtlSeconds() {
        return config.getTtlSeconds();
    }

    private byte[] sign(String payload) {
        try {
            Mac mac = Mac.getInstance(ALGORITHM);
            mac.init(new SecretKeySpec(config.getKey(), ALGORITHM));
            return mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("相册令牌签名失败", e);
        }
    }
}
