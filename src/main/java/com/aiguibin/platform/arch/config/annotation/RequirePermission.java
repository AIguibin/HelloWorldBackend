package com.aiguibin.platform.arch.config.annotation;

import java.lang.annotation.*;

/**
 * 权限控制注解
 * 用于标记需要权限检查的API方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识数组
     * @return 权限标识列表
     */
    String[] value() default {};

    /**
     * 是否需要登录
     * @return 是否需要登录
     */
    boolean requireLogin() default true;

    /**
     * 权限检查失败时的错误信息
     * @return 错误信息
     */
    String errorMessage() default "您没有权限执行此操作";
}