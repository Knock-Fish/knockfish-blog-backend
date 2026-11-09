package com.knockfish.service;

import com.knockfish.vo.gallery.CaptchaVO;

/**
 * 相册解锁用的验证码与失败计数。
 *
 * <p>验证码一次性消费、绑定 IP、2 分钟过期；每 (相册, IP) 连错 5 次锁 10 分钟。
 */
public interface CaptchaService {

    /** 生成一张 4 位字符验证码，返回 id 与 base64 图片 */
    CaptchaVO create(String ip);

    /**
     * 校验并立即作废验证码（无论对错都不能重复用同一张）。
     * 校验不通过抛 400，交由调用方统一提示「密码或验证码不正确」。
     */
    void verifyAndConsume(String captchaId, String code, String ip);

    /** 该 (相册, IP) 是否已被锁定 */
    boolean isLocked(Long albumId, String ip);

    /** 记一次失败，返回累计次数 */
    int recordFail(Long albumId, String ip);

    /** 解锁成功后清零 */
    void clearFail(Long albumId, String ip);

    /** 允许的最大连续失败次数，用于提示「还可尝试 N 次」 */
    int getMaxFail();
}
