package com.knockfish.security;

import com.knockfish.annotation.RequiresPermission;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义权限校验过滤器
 * 配合 @RequiresPermission 注解实现接口细粒度权限控制
 * 校验规则：先完成JWT认证，再校验接口所需权限
 */
@Component
@Slf4j
public class PermissionFilter {

    /**
     * 核心权限校验逻辑
     * @param request 请求对象
     * @param response 响应对象
     * @param filterChain 过滤器链
     * @return true=继续执行链路，false=终止请求
     * @throws ServletException  Servlet异常
     * @throws IOException IO异常
     */
    public boolean doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. 从安全上下文获取当前登录认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 未认证/匿名用户，直接放行，交给后续登录/认证逻辑处理
        if (authentication == null || !authentication.isAuthenticated()) {
            filterChain.doFilter(request, response);
            return true;
        }

        // 2. 获取当前请求对应的控制器方法处理器
        HandlerMethod handlerMethod = getHandlerMethod(request);
        // 非接口请求（静态资源、错误页等），无需权限校验，直接放行
        if (handlerMethod == null) {
            filterChain.doFilter(request, response);
            return true;
        }

        // 3. 获取接口上的 @RequiresPermission 权限注解
        RequiresPermission requiresPermission = getRequiresPermission(handlerMethod);
        // 接口未标注权限注解，代表公开接口，直接放行
        if (requiresPermission == null) {
            filterChain.doFilter(request, response);
            return true;
        }

        // 4. 获取注解中定义的接口所需权限数组
        String[] requiredPermissions = requiresPermission.value();
        if (requiredPermissions == null || requiredPermissions.length == 0) {
            filterChain.doFilter(request, response);
            return true;
        }

        // 5. 转换为自定义用户详情对象，获取用户权限列表
        CustomUserDetails userDetails = null;
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            userDetails = (CustomUserDetails) authentication.getPrincipal();
        }

        // 无法解析用户信息，直接返回403无权访问
        if (userDetails == null) {
            log.warn("权限校验失败：无法解析当前登录用户信息");
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权访问");
            return false;
        }

        // 当前用户拥有的所有权限编码
        List<String> userPermissions = userDetails.getPermissions();
        // 获取注解配置：是否需要拥有全部权限
        boolean allRequired = requiresPermission.allRequired();

        // 6. 执行权限比对
        boolean hasPermission = checkPermissions(userPermissions, requiredPermissions, allRequired);
        if (!hasPermission) {
            log.warn("用户[{}]权限不足，接口要求权限：{}", userDetails.getUsername(), Arrays.toString(requiredPermissions));
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权访问");
            return false;
        }

        // 权限校验通过，继续执行过滤器链
        log.debug("用户[{}]权限校验通过", userDetails.getUsername());
        filterChain.doFilter(request, response);
        return true;
    }

    /**
     * 从请求域中获取当前请求对应的 HandlerMethod（控制器方法）
     * @param request 请求对象
     * @return 控制器方法处理器，非接口请求返回null
     */
    private HandlerMethod getHandlerMethod(HttpServletRequest request) {
        try {
            // Spring MVC 存放匹配到的处理器属性名
            return (HandlerMethod) request.getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 解析 @RequiresPermission 注解
     * 优先级：方法上注解 > 控制器类上注解
     * @param handlerMethod 方法处理器
     * @return 权限注解实例，无注解返回null
     */
    private RequiresPermission getRequiresPermission(HandlerMethod handlerMethod) {
        // 优先取方法上的注解
        if (handlerMethod.hasMethodAnnotation(RequiresPermission.class)) {
            return handlerMethod.getMethodAnnotation(RequiresPermission.class);
        }
        // 方法无注解，再取控制器类上的注解
        Class<?> controllerClass = handlerMethod.getBeanType();
        if (controllerClass.isAnnotationPresent(RequiresPermission.class)) {
            return controllerClass.getAnnotation(RequiresPermission.class);
        }
        // 类和方法都无注解
        return null;
    }

    /**
     * 权限比对核心方法
     * @param userPermissions 当前用户拥有的权限集合
     * @param requiredPermissions 接口要求的权限数组
     * @param allRequired true=需全部拥有(AND)，false=拥有任意一个即可(OR)
     * @return true=有权限，false=无权限
     */
    private boolean checkPermissions(List<String> userPermissions, String[] requiredPermissions, boolean allRequired) {
        // 用户无任何权限，直接校验失败
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }

        if (allRequired) {
            // AND 逻辑：必须包含所有要求权限
            for (String requiredPermission : requiredPermissions) {
                if (!userPermissions.contains(requiredPermission)) {
                    return false;
                }
            }
            return true;
        } else {
            // OR 逻辑：包含任意一个权限即可
            for (String requiredPermission : requiredPermissions) {
                if (userPermissions.contains(requiredPermission)) {
                    return true;
                }
            }
            return false;
        }
    }
}