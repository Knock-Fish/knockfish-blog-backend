package com.knockfish.utils;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 取客户端 IP：优先 X-Forwarded-For（生产走 nginx 反代），否则用 remoteAddr。
 * 只用于验证码绑定与失败计数，不参与安全决策之外的事情。
 */
public final class IpUtil {

    private IpUtil() {
    }

    public static String resolve(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma > 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return realIp.trim();
        }
        return request.getRemoteAddr();
    }
}
