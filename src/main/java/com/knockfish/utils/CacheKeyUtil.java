package com.knockfish.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public final class  CacheKeyUtil {

    /**
     * 原始指纹超过该长度则压缩为定长摘要。
     * Redis 官方建议 key 尽量短（省内存、省带宽、更快过期扫描），
     * 120 足以容纳绝大多数「方法名 + 查询条件 + 分页」组合。
     */
    private static final int MAX_RAW_KEY_LENGTH = 120;

    /** 摘要 key 前缀，用于肉眼区分原始 key 与压缩 key */
    private static final String DIGEST_PREFIX = "~";

    /** 取 SHA-256 前 16 字节（128 bit）转 hex，碰撞概率可忽略 */
    private static final int DIGEST_BYTES = 16;

    private static final char[] HEX = "0123456789abcdef".toCharArray();

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private static final ObjectWriter WRITER = MAPPER.writer();

    /**
     * MessageDigest 非线程安全，且 getInstance 涉及 JCA/SPI 查找，
     * 用 ThreadLocal 复用（Web 容器线程数有限，不会泄漏）。
     */
    private static final ThreadLocal<MessageDigest> SHA256 =
            ThreadLocal.withInitial(CacheKeyUtil::createDigest);

    private CacheKeyUtil() {
    }

    public static String fingerprint(Object... objs) {
        if (objs == null || objs.length == 0) {
            return "empty";
        }
        StringBuilder sb = new StringBuilder(64);
        for (int i = 0; i < objs.length; i++) {
            if (i > 0) {
                sb.append(':');
            }
            append(sb, objs[i]);
        }
        return sb.length() <= MAX_RAW_KEY_LENGTH ? sb.toString() : DIGEST_PREFIX + sha256Hex(sb);
    }

    private static void append(StringBuilder sb, Object o) {
        if (o == null) {
            sb.append("null");
            return;
        }
        if (o instanceof CharSequence cs) {
            if (isSafeRaw(cs)) {
                sb.append(cs);
            } else {
                // 空串或含分隔符：交给 JSON 加引号，避免与其它参数拼接后产生歧义
                appendJson(sb, o);
            }
            return;
        }
        if (o instanceof Number || o instanceof Boolean || o instanceof Character) {
            sb.append(o);
            return;
        }
        if (o instanceof Enum<?> e) {
            sb.append(e.name());
            return;
        }
        appendJson(sb, o);
    }

    private static boolean isSafeRaw(CharSequence cs) {
        if (cs.isEmpty()) {
            return false;
        }
        for (int i = 0; i < cs.length(); i++) {
            if (cs.charAt(i) == ':') {
                return false;
            }
        }
        return true;
    }

    private static void appendJson(StringBuilder sb, Object o) {
        try {
            WRITER.writeValue(new StringBuilderWriter(sb), o);
        } catch (IOException e) {
            log.warn("CacheKeyUtil 序列化失败，降级为 toString：{}", o.getClass().getName());
            log.debug("CacheKeyUtil 序列化失败详情", e);
            sb.append(o.getClass().getSimpleName()).append('=').append(o);
        }
    }

    private static String sha256Hex(CharSequence raw) {
        MessageDigest md = SHA256.get();
        md.reset();
        md.update(raw.toString().getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        char[] out = new char[DIGEST_BYTES * 2];
        for (int i = 0; i < DIGEST_BYTES; i++) {
            int b = digest[i] & 0xff;
            out[i * 2] = HEX[b >>> 4];
            out[i * 2 + 1] = HEX[b & 0x0f];
        }
        return new String(out);
    }

    private static MessageDigest createDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("当前 JDK 不支持 SHA-256", e);
        }
    }

    /** 让 Jackson 直接写进 StringBuilder，避免 writeValueAsString 的中间 String 分配 */
    private static final class StringBuilderWriter extends Writer {
        private final StringBuilder sb;

        StringBuilderWriter(StringBuilder sb) {
            this.sb = sb;
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            sb.append(cbuf, off, len);
        }

        @Override
        public void write(String str, int off, int len) {
            sb.append(str, off, len);
        }

        @Override
        public void write(int c) {
            sb.append((char) c);
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }
    }
}
