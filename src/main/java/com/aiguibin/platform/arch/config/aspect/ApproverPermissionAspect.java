package com.aiguibin.platform.arch.config.aspect;

import com.aiguibin.platform.arch.config.annotation.RequireApprover;
import com.aiguibin.platform.arch.service.ApprovalService;
import com.aiguibin.platform.arch.service.PermissionService;
import com.aiguibin.platform.arch.service.UserService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import java.security.AccessControlException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 审批人权限切面组件
 * 实现审批人权限校验逻辑，确保只有合法审批人才能处理审批任务
 */
@Aspect
@Component
public class ApproverPermissionAspect {

    @Resource
    private ApprovalService approvalService;

    @Resource
    private PermissionService permissionService;

    @Resource
    private UserService userService;

    /**
     * 定义切入点，匹配所有标注@RequireApprover的方法
     */
    @Pointcut("@annotation(com.aiguibin.platform.arch.config.annotation.RequireApprover)")
    public void approvalPermissionPointcut() {
    }

    /**
     * 前置通知，在方法执行前进行审批人权限校验
     * @param joinPoint 连接点
     * @param requireApprover 审批人权限注解
     */
    @Before("approvalPermissionPointcut() && @annotation(requireApprover)")
    public void beforeApprovalMethod(JoinPoint joinPoint, RequireApprover requireApprover) {
        // 1. 获取当前用户信息
        String currentUserNum = getCurrentUserNum();
        if (currentUserNum == null) {
            throw new AccessControlException("用户未登录");
        }

        // 2. 获取任务ID
        Long taskId = getTaskIdFromParams(joinPoint, requireApprover);
        if (taskId == null) {
            throw new AccessControlException("无法获取审批任务ID");
        }

        // 3. 调用权限服务检查当前用户是否为合法审批人
        boolean isApprover = permissionService.isLegalApprover(currentUserNum, String.valueOf(taskId));
        if (!isApprover) {
            throw new AccessControlException(requireApprover.errorMessage());
        }
    }

    /**
     * 获取当前用户编号
     * @return 当前用户编号
     */
    private String getCurrentUserNum() {
        // 简化实现，实际应从SecurityContextHolder获取认证用户信息
        // 这里假设系统使用Spring Security，且用户信息存储在SecurityContextHolder中
        return "99999"; // 测试用，实际应替换为真实获取逻辑
    }

    /**
     * 从方法参数中获取任务ID
     * @param joinPoint 连接点
     * @param requireApprover 审批人权限注解
     * @return 任务ID
     */
    private Long getTaskIdFromParams(JoinPoint joinPoint, RequireApprover requireApprover) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Parameter[] parameters = method.getParameters();
        Object[] args = joinPoint.getArgs();

        // 遍历参数，查找任务ID参数
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (requireApprover.taskIdParam().equals(parameter.getName())) {
                Object arg = args[i];
                if (arg instanceof Long) {
                    return (Long) arg;
                } else if (arg instanceof String) {
                    try {
                        return Long.parseLong((String) arg);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                }
            }
        }

        return null;
    }
}