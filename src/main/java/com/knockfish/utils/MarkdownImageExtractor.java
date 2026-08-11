package com.knockfish.utils;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Markdown 图片链接提取工具类
 *
 * 支持以下图片语法：
 * <ul>
 *     <li>行内式：![alt](url) 或 ![alt](url "title")</li>
 *     <li>HTML 嵌入式：&lt;img src="url" /&gt;</li>
 *     <li>引用式：![alt][id] 配合 [id]: url 定义</li>
 * </ul>
 */
public class MarkdownImageExtractor {

    private MarkdownImageExtractor() {
    }

    /** 行内式图片：![alt](url) 或 ![alt](url "title") */
    private static final Pattern INLINE_IMAGE =
            Pattern.compile("!\\[[^]]*]\\(([^\\s)]+)(?:\\s+\"[^\"]*\")?\\)");

    /** HTML img 标签：兼容单双引号、属性顺序 */
    private static final Pattern HTML_IMG =
            Pattern.compile("<img[^>]*\\bsrc\\s*=\\s*[\"']([^\"']+)[\"'][^>]*>",
                    Pattern.CASE_INSENSITIVE);

    /** 引用式定义：[id]: url */
    private static final Pattern REFERENCE_DEF =
            Pattern.compile("^\\s*\\[([^]]+)]:\\s*(\\S+)", Pattern.MULTILINE);

    /** 引用式使用：![alt][id] */
    private static final Pattern REFERENCE_USE =
            Pattern.compile("!\\[[^]]*]\\[([^]]+)]");

    /**
     * 提取所有图片链接（按出现顺序去重）
     *
     * @param markdown Markdown 文本
     * @return 图片链接列表，输入为空时返回空列表
     */
    public static List<String> extractUrls(String markdown) {
        if (StrUtil.isBlank(markdown)) {
            return new ArrayList<>();
        }
        Map<String, Boolean> seen = new LinkedHashMap<>();
        for (ImageInfo info : extractAll(markdown)) {
            seen.put(info.url(), Boolean.TRUE);
        }
        return new ArrayList<>(seen.keySet());
    }

    /**
     * 提取所有图片对应的 R2 file key（按出现顺序去重）
     * 例如 https://fishbarn.cn/blog/1/article/xxx.webp -&gt; blog/1/article/xxx.webp
     *
     * @param markdown Markdown 文本
     * @return file key 列表，输入为空时返回空列表
     */
    public static List<String> extractFileKeys(String markdown) {
        if (StrUtil.isBlank(markdown)) {
            return new ArrayList<>();
        }
        Map<String, Boolean> seen = new LinkedHashMap<>();
        for (ImageInfo info : extractAll(markdown)) {
            String key = UrlUtil.extractFileKeyFromUrl(info.url());
            if (StrUtil.isNotBlank(key)) {
                seen.put(key, Boolean.TRUE);
            }
        }
        return new ArrayList<>(seen.keySet());
    }

    /**
     * 提取所有图片信息（保留出现顺序，不去重）
     *
     * @param markdown Markdown 文本
     * @return 图片信息列表，输入为空时返回空列表
     */
    public static List<ImageInfo> extract(String markdown) {
        if (StrUtil.isBlank(markdown)) {
            return new ArrayList<>();
        }
        return extractAll(markdown);
    }

    /**
     * 提取所有图片 URL 与 alt 文本的映射（同 URL 以首次出现为准）
     *
     * @param markdown Markdown 文本
     * @return url -&gt; alt 映射，输入为空时返回空 Map
     */
    public static Map<String, String> extractUrlAltMap(String markdown) {
        Map<String, String> result = new LinkedHashMap<>();
        if (StrUtil.isBlank(markdown)) {
            return result;
        }
        for (ImageInfo info : extractAll(markdown)) {
            result.putIfAbsent(info.url(), info.alt());
        }
        return result;
    }

    private static List<ImageInfo> extractAll(String markdown) {
        List<ImageInfo> list = new ArrayList<>();

        // 行内式
        Matcher inline = INLINE_IMAGE.matcher(markdown);
        while (inline.find()) {
            list.add(new ImageInfo(inline.group(1), extractAlt(inline.group())));
        }

        // HTML img
        Matcher html = HTML_IMG.matcher(markdown);
        while (html.find()) {
            list.add(new ImageInfo(html.group(1), extractAltAttr(html.group())));
        }

        // 引用式：先收集定义，再替换占位
        Map<String, String> refMap = new LinkedHashMap<>();
        Matcher def = REFERENCE_DEF.matcher(markdown);
        while (def.find()) {
            refMap.put(def.group(1).trim(), def.group(2));
        }
        if (!refMap.isEmpty()) {
            Matcher use = REFERENCE_USE.matcher(markdown);
            while (use.find()) {
                String id = use.group(1).trim();
                String url = refMap.get(id);
                if (StrUtil.isNotBlank(url)) {
                    list.add(new ImageInfo(url, extractAlt(use.group())));
                }
            }
        }
        return list;
    }

    /** 从 ![alt](url) 整段文本中提取 alt 文本 */
    private static String extractAlt(String segment) {
        int start = segment.indexOf('[');
        int end = segment.indexOf(']');
        if (start < 0 || end < 0 || end <= start) {
            return "";
        }
        return segment.substring(start + 1, end);
    }

    /** 从 &lt;img&gt; 标签中提取 alt 属性值 */
    private static String extractAltAttr(String tag) {
        Matcher m = Pattern.compile("\\balt\\s*=\\s*[\"']([^\"']*)[\"']", Pattern.CASE_INSENSITIVE)
                .matcher(tag);
        return m.find() ? m.group(1) : "";
    }

    /**
     * 图片信息
     */
    public record ImageInfo(String url, String alt) {
        public ImageInfo(String url, String alt) {
            this.url = url;
            this.alt = alt == null ? "" : alt;
        }

        @Override
        public String toString() {
            return "ImageInfo{url='" + url + "', alt='" + alt + "'}";
        }
    }
}
