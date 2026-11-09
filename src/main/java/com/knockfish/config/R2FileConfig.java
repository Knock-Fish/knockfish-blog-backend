package com.knockfish.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.time.Duration;

/**
 * Cloudflare R2 配置。
 *
 * <p>国内网络访问 {@code *.r2.cloudflarestorage.com} 经常出现 TCP 连不上（现象是
 * {@code Connect timed out}），所以这里支持可选的 HTTP 代理：
 * <pre>
 * cloud:
 *   aws:
 *     s3:
 *       proxy:
 *         host: 127.0.0.1
 *         port: 7890
 * </pre>
 * 同时把连接超时压到 5s，避免前端上传按钮卡十几秒。
 */
@Slf4j
@Component
@ConfigurationProperties(prefix = "cloud.aws.s3")
@Data
public class R2FileConfig {
    private String endpoint;
    private String region;
    private String bucketName;
    private String cdnDomain;
    private Credentials credentials;
    /** 可选代理；不配则直连 */
    private Proxy proxy;
    /** 连接超时（毫秒），默认 5s */
    private long connectTimeoutMillis = 5000;
    /** 读写超时（毫秒），默认 60s，大图上传需要 */
    private long socketTimeoutMillis = 60000;

    @Data
    public static class Credentials {
        private String accessKey;
        private String secretKey;
    }

    @Data
    public static class Proxy {
        private String host;
        private Integer port;
        private String username;
        private String password;
    }

    /**
     * 启动自检：凭据为空时不阻止启动（其它功能可用），但提前把原因打到日志，
     * 避免到第一次上传才看到一个 "Access key ID cannot be blank" 的 NPE。
     */
    @PostConstruct
    public void checkCredentials() {
        boolean missing = credentials == null
                || credentials.getAccessKey() == null || credentials.getAccessKey().isBlank()
                || credentials.getSecretKey() == null || credentials.getSecretKey().isBlank();
        if (missing) {
            log.warn("R2 凭据未配置：cloud.aws.s3.credentials.access-key/secret-key 为空，"
                    + "所有上传接口不可用。设置环境变量 R2_ACCESS_KEY / R2_SECRET_KEY，"
                    + "或以 --spring.profiles.active=local 启动（本地 application-local.yml 已存有凭据）");
        } else {
            log.info("R2 存储配置就绪：bucket={} endpoint={} proxy={}",
                    bucketName, endpoint, proxy != null && proxy.getHost() != null ? proxy.getHost() + ":" + proxy.getPort() : "直连");
        }
    }

    @Bean
    public S3Client s3Client() {
        ApacheHttpClient.Builder httpClient = ApacheHttpClient.builder()
                .connectionTimeout(Duration.ofMillis(connectTimeoutMillis))
                .socketTimeout(Duration.ofMillis(socketTimeoutMillis));

        if (proxy != null && proxy.getHost() != null && !proxy.getHost().isBlank()) {
            int port = proxy.getPort() == null ? 7890 : proxy.getPort();
            // SDK v2 的 ProxyConfiguration 用 endpoint 指定代理地址
            ProxyConfiguration.Builder proxyConfig = ProxyConfiguration.builder()
                    .endpoint(URI.create("http://" + proxy.getHost() + ":" + port));
            if (proxy.getUsername() != null && !proxy.getUsername().isBlank()) {
                proxyConfig.username(proxy.getUsername()).password(proxy.getPassword());
            }
            httpClient.proxyConfiguration(proxyConfig.build());
            log.info("R2 通过代理访问：{}:{}", proxy.getHost(), port);
        }

        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                // R2 用路径式寻址，避免依赖 bucket 子域名的 DNS
                .forcePathStyle(true)
                .httpClientBuilder(httpClient)
                .credentialsProvider(() -> AwsBasicCredentials.create(
                        this.credentials.getAccessKey(),
                        this.credentials.getSecretKey()
                ))
                .region(Region.of(region))
                .build();
    }
}
