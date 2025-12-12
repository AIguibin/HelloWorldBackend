package com.aiguibin.platform.arch.service;

import java.util.List;
import java.util.Map;

/**
 * 权限服务接口.
 * 提供权限验证、字段权限检查等功能.
 */
public interface PermissionService {

    /**
     * 检查用户是否拥有指定权限.
     * @param permissionKey 权限关键字.
     * @param userNum 用户编号.
     * @return 是否拥有该权限.
     */
    boolean hasPermission(String permissionKey, String userNum);

    /**
     * 检查角色权限.
     * @param roleCode 角色代码.
     * @param userNum 用户编号.
     * @return 是否拥有该角色权限.
     */
    boolean checkRolePermission(String roleCode, String userNum);

    /**
     * 检查字段权限.
     * @param userNum 用户编号.
     * @param entityType 实体类型.
     * @param fieldName 字段名称.
     * @param permType 权限类型.
     * @return 是否拥有该字段权限.
     */
    boolean checkFieldPermission(String userNum, String entityType, String fieldName, String permType);

    /**
     * 获取可编辑字段列表.
     * @param userNum 用户编号.
     * @param entityType 实体类型.
     * @param currentStatus 当前状态.
     * @return 可编辑字段列表.
     */
    List<String> getEditableFields(String userNum, String entityType, String currentStatus);

    /**
     * 过滤可编辑字段.
     * @param userNum 用户编号.
     * @param entityType 实体类型.
     * @param fieldValues 字段值映射.
     * @param currentStatus 当前状态.
     * @return 过滤后的字段值映射.
     */
    Map<String, Object> filterEditableFields(String userNum, String entityType, Map<String, Object> fieldValues, String currentStatus);

    /**
     * 获取用户角色列表.
     * @param userNum 用户编号.
     * @return 角色列表.
     */
    List<String> getUserRoles(String userNum);

    /**
     * 检查是否为合法审批人.
     * @param userNum 用户编号.
     * @param taskId 任务ID.
     * @return 是否为合法审批人.
     */
    boolean isLegalApprover(String userNum, String taskId);

    /**
     * 检查是否拥有审批权限.
     * @param userNum 用户编号.
     * @param taskId 任务ID.
     * @param nodeId 节点ID.
     * @return 是否拥有审批权限.
     */
    boolean hasApprovalPermission(String userNum, String taskId, String nodeId);
}
