package com.knockfish.security;

import com.knockfish.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器（只解析、不拦截）。
 *
 * <p>职责边界：
 * <ul>
 *   <li>有合法 Token → 解析出用户并写入 SecurityContext；</li>
 *   <li>没有 Token 或解析失败 → <b>不返回 401</b>，直接放行，由 SecurityFilterChain 的
 *       路径规则决定该请求是否需要认证（公开接口照常通过，受保护接口由框架返回 401）。</li>
 * </ul>
 *
 * <p>这样"哪些接口公开"只有一处真相（{@code SecurityConfig} 的路径规则），
 * 不再需要在过滤器里判断路径、解析 HandlerMethod 或读注解。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // 无 Token：交给 Security 决定（公开接口放行，受保护接口返回 401）
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            // 已认证（例如同一请求内二次进入）则不重复解析
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                if (userId != null) {
                    UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                    if (userDetails != null) {
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("JWT 认证通过 userId={} path={}", userId, request.getRequestURI());
                    }
                }
            }
        } catch (Exception e) {
            // Token 过期/签名错误：清空上下文，仍不拦截；受保护接口后续会得到 401
            SecurityContextHolder.clearContext();
            log.debug("JWT 解析失败 path={} reason={}", request.getRequestURI(), e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
