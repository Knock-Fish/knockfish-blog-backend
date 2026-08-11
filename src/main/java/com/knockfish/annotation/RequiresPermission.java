package com.knockfish.annotation;

import java.lang.annotation.*;

/**
 * 自定义权限校验注解
 * 用于标注需要特定权限才能访问的方法
 * 权限编码格式：项目:资源:操作，如 blog:article:add
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * 所需的权限编码列表
     * 格式：项目:资源:操作，如 blog:article:add
     */
    String[] value() default {};
    
    /**
     * 是否需要所有权限都满足（默认为true）
     * true: 必须拥有所有指定权限才能访问（AND关系）
     * false: 拥有任意一个权限即可访问（OR关系）
     */
    boolean allRequired() default true;
}