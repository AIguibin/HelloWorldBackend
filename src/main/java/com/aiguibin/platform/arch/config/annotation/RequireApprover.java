package com.aiguibin.platform.arch.config.annotation;

import java.lang.annotation.*;

/**
 * 审批人权限控制注解
 * 用于标记需要审批人权限检查的API方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireApprover {

    /**
     * 审批节点标识
     * @return 审批节点标识
     */
    String nodeId() default "";

    /**
     * 是否需要登录
     * @return 是否需要登录
     */
    boolean requireLogin() default true;

    /**
     * 权限检查失败时的错误信息
     * @return 错误信息
     */
    String errorMessage() default "您不是该审批节点的合法审批人";

    /**
     * 任务ID参数名
     * @return 任务ID参数名
     */
    String taskIdParam() default "taskId";
}