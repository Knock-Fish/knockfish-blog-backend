package com.knockfish.config;

import com.knockfish.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security 安全全局配置类
 * 适配前后端分离 + JWT无状态认证 + 跨域处理
 */
@Configuration          // 标记为Spring配置类
@EnableWebSecurity      // 开启Spring Security Web安全功能
@RequiredArgsConstructor // lombok注解：自动注入final成员变量，无需手写构造器
public class SecurityConfig {

    /**
     * 自定义JWT认证过滤器，用于解析请求头中的Token并完成认证
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码加密器 Bean
     * 使用BCrypt算法进行密码哈希加密，强度10（取值4~31，数值越大加密越慢、安全性越高）
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    /**
     * 跨域CORS配置 Bean
     * 解决前后端分离跨域请求、线上环境跨域403问题
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 1. 允许跨域请求的来源域名（本地开发 + 线上正式域名）
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3307",
                "https://admin.fishbarn.cn",
                "https://blog.fishbarn.cn"
        ));

        // 2. 允许的所有HTTP请求方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // 3. 允许客户端携带的所有请求头（JWT的Authorization头在此范围内）
        configuration.setAllowedHeaders(List.of("*"));

        // 4. 暴露响应头，允许前端JS读取指定响应头（用于前端获取Token）
        configuration.setExposedHeaders(Arrays.asList("Authorization", "token"));

        // 5. 允许跨域请求携带Cookie、凭证信息
        configuration.setAllowCredentials(true);

        // 6. 预检请求有效期（单位：秒），3600秒=1小时，减少OPTIONS预检请求次数
        configuration.setMaxAge(3600L);

        // 绑定跨域配置到全局所有接口
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 安全过滤链核心配置
     * 定义安全规则、接口权限、过滤器、会话策略等
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 启用自定义CORS跨域配置
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 禁用CSRF防护：前后端分离+JWT无状态架构，无需CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 设置会话策略：无状态，不创建、不使用HttpSession
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 接口授权规则配置
                .authorizeHttpRequests(auth -> auth
                        // 登录接口：直接放行，无需认证
                        .requestMatchers("/api/auth/login/**").permitAll()
                        // 文件上传/访问接口：放行
                        .requestMatchers("/api/r2-file/**").permitAll()
                        // Swagger接口文档相关路径：放行，方便调试
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**",
                                "/swagger-resources/**", "/webjars/**").permitAll()
                        // 系统全局错误页面：放行
                        .requestMatchers("/error").permitAll()
                        // 其余所有接口：必须经过认证才能访问
//                        .anyRequest().authenticated()
                        // 其余全部交给过滤器判断
                        .anyRequest().permitAll()
                )
                // 在账号密码认证过滤器之前，添加自定义JWT过滤器
                // 优先解析Token，提前完成身份认证
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // 构建并返回安全过滤链
        return http.build();
    }
}