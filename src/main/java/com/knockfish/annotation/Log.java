package com.knockfish.annotation;

import java.lang.annotation.*;

/**
 * 自定义日志注解
 * 用于标记需要记录日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    
    /**
     * 操作描述
     */
    String value() default "";
    
    /**
     * 是否记录请求参数
     */
    boolean recordParams() default true;
    
    /**
     * 是否记录返回结果
     */
    boolean recordResult() default false;
    
    /**
     * 是否记录执行时间
     */
    boolean recordTime() default true;
}
