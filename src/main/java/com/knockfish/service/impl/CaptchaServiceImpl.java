package com.knockfish.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.knockfish.exception.CustomException;
import com.knockfish.service.CaptchaService;
import com.knockfish.vo.gallery.CaptchaVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

/**
 * 验证码与失败计数的单机实现。
 *
 * <p>验证码与失败次数都放进程内 Caffeine，不依赖 Redis。
 * 以后要多实例部署，把两个 Cache 换成 Redis 实现即可（对外方法不变）。
 */
@Slf4j
@Service
public class CaptchaServiceImpl implements CaptchaService {

    private static final Duration CAPTCHA_TTL = Duration.ofMinutes(2);
    private static final Duration LOCK_TTL = Duration.ofMinutes(10);
    /** 连续失败多少次锁定 */
    private static final int MAX_FAIL = 5;

    private final Cache<String, CaptchaEntry> captchaCache = Caffeine.newBuilder()
            .expireAfterWrite(CAPTCHA_TTL)
            .maximumSize(20_000)
            .build();

    private final Cache<String, Integer> failCache = Caffeine.newBuilder()
            .expireAfterWrite(LOCK_TTL)
            .maximumSize(20_000)
            .build();

    @Override
    public CaptchaVO create(String ip) {
        LineCaptcha captcha = CaptchaUtil.createLineCaptcha(120, 40, 4, 20);
        String captchaId = UUID.randomUUID().toString().replace("-", "");
        captchaCache.put(captchaId, new CaptchaEntry(captcha.getCode(), ip));

        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaId(captchaId);
        vo.setImage("data:image/png;base64," + captcha.getImageBase64());
        return vo;
    }

    @Override
    public void verifyAndConsume(String captchaId, String code, String ip) {
        CaptchaEntry entry = captchaId == null ? null : captchaCache.getIfPresent(captchaId);
        if (entry != null) {
            captchaCache.invalidate(captchaId);
        }
        boolean ok = entry != null
                && code != null
                && entry.code().equalsIgnoreCase(code.trim())
                && entry.ip().equals(ip);
        if (!ok) {
            throw new CustomException(400, "验证码错误或已过期");
        }
    }

    @Override
    public boolean isLocked(Long albumId, String ip) {
        Integer count = failCache.getIfPresent(failKey(albumId, ip));
        return count != null && count >= MAX_FAIL;
    }

    @Override
    public int recordFail(Long albumId, String ip) {
        String key = failKey(albumId, ip);
        Integer current = failCache.getIfPresent(key);
        int next = current == null ? 1 : current + 1;
        failCache.put(key, next);
        return next;
    }

    @Override
    public void clearFail(Long albumId, String ip) {
        failCache.invalidate(failKey(albumId, ip));
    }

    @Override
    public int getMaxFail() {
        return MAX_FAIL;
    }

    private String failKey(Long albumId, String ip) {
        return "album:" + albumId + ":ip:" + ip;
    }

    private record CaptchaEntry(String code, String ip) {
    }
}
