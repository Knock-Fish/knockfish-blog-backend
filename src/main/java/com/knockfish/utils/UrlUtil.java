package com.knockfish.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlUtil {
    /**
     * 从图片完整 URL 中提取后端存储的 key
     */
    public static String extractFileKeyFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        try {
            URL uri = new URL(url);
            // 去掉路径开头的 /
            return uri.getPath().replaceFirst("/", "");
        } catch (MalformedURLException e) {
            // 不是合法 URL 直接返回原值
            return url;
        }
    }
}