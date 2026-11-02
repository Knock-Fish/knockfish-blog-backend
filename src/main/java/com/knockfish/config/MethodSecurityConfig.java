package com.knockfish.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * 方法级鉴权开关。
 *
 * <p>开启后，Controller 上的 {@code @PreAuthorize("hasAuthority('PERMISSION_blog:xxx:yyy')")}
 * 才会真正生效——权限码来自 {@code CustomUserDetails#getAuthorities()}（形如 PERMISSION_blog:article:add）。
 *
 * <p>这替代了原来那个「从未被任何代码调用」的 PermissionFilter：
 * 权限判断交给 Spring Security，不再手写 HandlerMethod 解析。
 */
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
