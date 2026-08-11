package com.knockfish.security;

import com.knockfish.annotation.PublicApi;
import com.knockfish.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证过滤器
 * 继承 OncePerRequestFilter：保证一次请求只会执行一次过滤逻辑
 * 作用：解析请求头中的 JWT，完成用户认证并写入 Spring Security 上下文
 */
@Component
@RequiredArgsConstructor // 自动注入 final 成员变量
@Slf4j                // 日志注解
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    // JWT 工具类，用于解析 Token
    private final JwtUtil jwtUtil;
    // 用户信息查询服务，根据 userId 加载用户、角色、权限
    private final CustomUserDetailsService customUserDetailsService;
    // 注入 HandlerMapping
    private final List<HandlerMapping> handlerMappings;

    /**
     * 定义无需认证的公开接口路径
     * 支持 Ant 路径匹配：/** 通配、{id} 路径变量
     */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/api/auth/login",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/error"
    );

    /**
     * 过滤器核心逻辑
     * @param request  请求对象
     * @param response 响应对象
     * @param filterChain 过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        System.err.println("========== JwtAuthenticationFilter.doFilterInternal START ==========");
        System.err.println("请求URI: " + request.getRequestURI());
        // 获取当前请求 URI 和请求方式
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        // 添加日志：记录所有请求
        log.info("=== 请求路径: {}, 方法: {}", requestPath, requestMethod);


        // 放行跨域 OPTIONS 预检请求
        if ("OPTIONS".equals(requestMethod)) {
            response.setStatus(HttpServletResponse.SC_OK);
            filterChain.doFilter(request, response);
            return;
        }

        System.err.println("=========公开接口==========");
        System.err.println(request);
        System.err.println(isPublicApiAnnotation(request));
        if (isPublicApiAnnotation(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 判断是否为公开路径，公开接口直接放行，不校验 Token
        if (isPublicPath(requestPath)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从请求头获取 Authorization
        String authHeader = request.getHeader("Authorization");
        // 校验 Token 格式：必须以 Bearer 开头
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("请求 {} Token 缺失或格式错误", requestPath);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 缺失或格式错误");
            return;
        }

        // 截取 Bearer 后面的真实 Token 字符串（Bearer 占7位：Bearer ）
        String token = authHeader.substring(7);

        try {
            // 解析 Token，获取用户ID
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                log.warn("请求 {} Token 解析失败，未获取到用户ID", requestPath);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 无效");
                return;
            }

            // 安全上下文未认证，则执行认证逻辑
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                // 根据 userId 加载用户信息、角色、权限
                UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                if (userDetails == null) {
                    log.warn("用户ID {} 不存在", userId);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户不存在");
                    return;
                }

                // 构建认证令牌：(用户主体, 凭证(此处存token), 权限集合)
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                token,
                                userDetails.getAuthorities()
                        );
                // 封装请求详情（IP、Session 等）
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将认证信息存入 Spring Security 上下文，后续接口可直接获取登录用户
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("用户 [{}] (ID:{}) JWT 认证通过", userDetails.getUsername(), userId);
            }

            // 认证完成，继续执行后续过滤器与接口
            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // 捕获所有 Token 异常：过期、签名错误、篡改、解析异常等
            log.error("Token 验证异常：{}，请求路径：{}", e.getMessage(), requestPath);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 无效或已过期");
        }
    }

    /**
     * 使用 Ant 路径匹配器，判断当前请求是否为公开接口
     * @param requestPath 当前请求路径
     * @return true=公开路径放行，false=需要校验Token
     */
    private boolean isPublicPath(String requestPath) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        // 遍历公开路径，任意一个匹配即返回 true
        return PUBLIC_PATHS.stream()
                .anyMatch(path -> pathMatcher.match(path, requestPath));
    }

    /**
     * 判断当前接口是否标记了 @PublicApi 注解
     * 优先级：方法注解 > 类注解
     */
    private boolean isPublicApiAnnotation(HttpServletRequest request) {
//        // 获取当前请求对应的控制器方法
//        Object handlerObj = request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
//        if (!(handlerObj instanceof HandlerMethod handlerMethod)) {
//            // 非接口请求（静态资源、异常等），不做注解判断
//            return false;
//        }
//
//        // 优先判断方法上的 @PublicApi
//        if (handlerMethod.hasMethodAnnotation(PublicApi.class)) {
//            return true;
//        }
//
//        // 再判断 Controller 类上的 @PublicApi
//        Class<?> controllerClass = handlerMethod.getBeanType();
//        return controllerClass.isAnnotationPresent(PublicApi.class);
        try {
            // 手动获取 HandlerMethod
            HandlerMethod handlerMethod = getHandlerMethod(request);

            if (handlerMethod == null) {
                return false;
            }

            System.err.println("找到 HandlerMethod: " + handlerMethod.getMethod().getName());

            // 优先判断方法上的 @PublicApi
            if (handlerMethod.hasMethodAnnotation(PublicApi.class)) {
                System.err.println("方法上有 @PublicApi 注解");
                return true;
            }

            // 再判断 Controller 类上的 @PublicApi
            Class<?> controllerClass = handlerMethod.getBeanType();
            if (controllerClass.isAnnotationPresent(PublicApi.class)) {
                System.err.println("类上有 @PublicApi 注解");
                return true;
            }

            System.err.println("没有找到 @PublicApi 注解");

        } catch (Exception e) {
            System.err.println("判断 @PublicApi 注解失败: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 手动获取 HandlerMethod
     * 遍历所有 HandlerMapping 找到能处理当前请求的 Handler
     */
    private HandlerMethod getHandlerMethod(HttpServletRequest request) {
        if (handlerMappings == null) {
            return null;
        }

        for (HandlerMapping handlerMapping : handlerMappings) {
            try {
                HandlerExecutionChain chain = handlerMapping.getHandler(request);
                if (chain.getHandler() instanceof HandlerMethod) {
                    return (HandlerMethod) chain.getHandler();
                }
            } catch (Exception e) {
                System.err.println("从 HandlerMapping 获取失败: " + e.getMessage());
            }
        }
        return null;
    }
}