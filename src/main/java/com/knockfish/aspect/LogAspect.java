package com.knockfish.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knockfish.annotation.Log;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

/**
 * 日志切面
 * 统一处理带有 @Log 注解的方法的日志记录
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LogAspect {

    private final ObjectMapper objectMapper;

    /**
     * 定义切点：所有带有 @Log 注解的方法
     */
    @Pointcut("@annotation(com.knockfish.annotation.Log)")
    public void logPointcut() {
    }

    /**
     * 环绕通知：在方法执行前后记录日志
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取 @Log 注解
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation == null) {
            return joinPoint.proceed();
        }

        // 获取类名和方法名
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();
        String operation = logAnnotation.value().isEmpty() ? methodName : logAnnotation.value();

        // 获取请求信息
        HttpServletRequest request = getRequest();
        String requestURI = request != null ? request.getRequestURI() : "N/A";
        String httpMethod = request != null ? request.getMethod() : "N/A";

        // 记录请求日志
        if (logAnnotation.recordParams()) {
            Object[] args = joinPoint.getArgs();
            String params = getParams(args);
            log.info("[{}] 请求开始 - {} {} - {}.{} - 参数: {}", 
                    operation, httpMethod, requestURI, className, methodName, params);
        } else {
            log.info("[{}] 请求开始 - {} {} - {}.{}", 
                    operation, httpMethod, requestURI, className, methodName);
        }

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        
        try {
            // 执行目标方法
            Object result = joinPoint.proceed();
            
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录成功日志
            if (logAnnotation.recordResult()) {
                String resultStr = getResult(result);
                if (logAnnotation.recordTime()) {
                    log.info("[{}] 请求成功 - 执行时间: {}ms - 返回结果: {}", 
                            operation, executionTime, resultStr);
                } else {
                    log.info("[{}] 请求成功 - 返回结果: {}", operation, resultStr);
                }
            } else {
                if (logAnnotation.recordTime()) {
                    log.info("[{}] 请求成功 - 执行时间: {}ms", operation, executionTime);
                } else {
                    log.info("[{}] 请求成功", operation);
                }
            }
            
            return result;
            
        } catch (Throwable e) {
            // 计算执行时间
            long executionTime = System.currentTimeMillis() - startTime;
            
            // 记录异常日志
            log.error("[{}] 请求异常 - 执行时间: {}ms - 异常信息: {}", 
                    operation, executionTime, e.getMessage(), e);
            
            throw e;
        }
    }

    /**
     * 获取当前请求
     */
    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes = 
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取参数字符串
     */
    private String getParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "无";
        }
        
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                // 过滤掉 HttpServletRequest 和 HttpServletResponse
                if (args[i] instanceof HttpServletRequest) {
                    sb.append("[Request]");
                } else if (args[i] instanceof jakarta.servlet.http.HttpServletResponse) {
                    sb.append("[Response]");
                } else {
                    sb.append(objectMapper.writeValueAsString(args[i]));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "参数序列化失败";
        }
    }

    /**
     * 获取返回结果字符串
     */
    private String getResult(Object result) {
        if (result == null) {
            return "null";
        }
        
        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            return "结果序列化失败";
        }
    }
}
