package com.knockfish.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis Spring Cache 全局配置：
 * 1. 开启 @EnableCaching
 * 2. 使用 GenericJackson2JsonRedisSerializer 做值序列化（避免 JDK 序列化兼容性问题）
 * 3. 针对各业务 cacheName 配置独立 TTL（按业务热点/更新频率设置）
 */
@Slf4j
@Configuration
@EnableCaching
public class RedisCacheConfig implements CachingConfigurer {

    /**
     * 各缓存名 -> TTL（秒）
     */
    private static final Map<String, Duration> CACHE_TTLS = new HashMap<>();

    static {
        // 用户信息：更新频率低，30 分钟
        CACHE_TTLS.put("userCache", Duration.ofMinutes(30));
        // 文章列表/归档：更新频率中等，10 分钟
        CACHE_TTLS.put("articleCache", Duration.ofMinutes(10));
        // 文章详情：热点数据，15 分钟，写操作触发 evict 实时失效
        CACHE_TTLS.put("articleDetailCache", Duration.ofMinutes(15));
        // 分类/标签：更新频率低，1 小时
        CACHE_TTLS.put("categoryCache", Duration.ofHours(1));
    }

    /**
     * 通用 RedisCacheConfiguration：前缀、序列化、TTL 默认值
     */
    private RedisCacheConfiguration defaultCacheConfig() {
        ObjectMapper om = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                .activateDefaultTyping(
                        LaissezFaireSubTypeValidator.instance,
                        ObjectMapper.DefaultTyping.NON_FINAL
                );

        GenericJackson2JsonRedisSerializer valueSerializer =
                new GenericJackson2JsonRedisSerializer(om);

        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))            // 默认 TTL 兜底
                .prefixCacheNameWith("blog:cache:")          // 统一 key 前缀：blog:cache:{cacheName}::
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer(StandardCharsets.UTF_8)))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(valueSerializer))
                .disableCachingNullValues();                 // unless = "#result == null" 双保险
    }

    /**
     * 为不同 cacheName 注册独立 TTL
     */
    @Bean
    public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
        return builder -> {
            RedisCacheConfiguration base = defaultCacheConfig();
            builder.cacheDefaults(base);
            CACHE_TTLS.forEach((name, ttl) ->
                    builder.withCacheConfiguration(name, base.entryTtl(ttl)));
        };
    }

    /**
     * 缓存故障降级：Redis 连不上时只记日志并回源数据库，不让业务接口 500。
     *
     * <p>默认的 SimpleCacheErrorHandler 会把 RedisConnectionFailureException 直接抛出，
     * 结果就是"Redis 抖一下，全站接口报错"。
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.warn("缓存读取失败，已回源。cache={} key={} reason={}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.warn("缓存写入失败，已忽略。cache={} key={} reason={}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.warn("缓存清除失败，已忽略。cache={} key={} reason={}", cache.getName(), key, e.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.warn("缓存清空失败，已忽略。cache={} reason={}", cache.getName(), e.getMessage());
            }
        };
    }
}
