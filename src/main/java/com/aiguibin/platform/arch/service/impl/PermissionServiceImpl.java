package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.aiguibin.platform.arch.entity.SysFieldPermission;
import com.aiguibin.platform.arch.entity.SysUserRole;
import com.aiguibin.platform.arch.mapper.SysFieldPermissionMapper;
import com.aiguibin.platform.arch.mapper.SysPermissionMapper;
import com.aiguibin.platform.arch.mapper.SysRolePermissionMapper;
import com.aiguibin.platform.arch.mapper.SysUserRoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 权限服务实现类.
 * 基于sys_permission和sys_field_permission表实现权限验证和字段权限检查.
 */
@Service
public final class PermissionServiceImpl implements PermissionService {

    /**
     * 系统权限Mapper.
     */
    @Resource
    private SysPermissionMapper sysPermissionMapper;

    /**
     * 系统角色权限Mapper.
     */
    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    /**
     * 系统用户角色Mapper.
     */
    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 系统字段权限Mapper.
     */
    @Resource
    private SysFieldPermissionMapper sysFieldPermissionMapper;

    @Override
    public boolean hasPermission(final String permissionKey, final String userNum) {
        // 1. 获取用户角色列表
        List<String> userRoles = getUserRoles(userNum);
        if (userRoles.isEmpty()) {
            return false;
        }

        // 2. 查询角色拥有的权限
        return sysPermissionMapper.hasPermission(userRoles, permissionKey);
    }

    @Override
    public boolean checkRolePermission(final String roleCode, final String userNum) {
        // 1. 获取用户角色列表
        List<String> userRoles = getUserRoles(userNum);
        // 2. 检查用户是否拥有指定角色
        return userRoles.contains(roleCode);
    }

    @Override
    public boolean checkFieldPermission(
            final String userNum,
            final String entityType,
            final String fieldName,
            final String permType) {
        // 1. 获取用户角色列表
        List<String> userRoles = getUserRoles(userNum);
        if (userRoles.isEmpty()) {
            return false;
        }

        // 2. 查询字段权限
        LambdaQueryWrapper<SysFieldPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysFieldPermission::getRoleCode, userRoles)
                .eq(SysFieldPermission::getEntityType, entityType)
                .eq(SysFieldPermission::getFieldName, fieldName)
                .eq(SysFieldPermission::getPermType, permType)
                .eq(SysFieldPermission::getStatus, 1)
                .eq(SysFieldPermission::getIsDeleted, 0);

        return sysFieldPermissionMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<String> getEditableFields(
            final String userNum,
            final String entityType,
            final String currentStatus) {
        // 1. 获取用户可编辑字段列表（权限类型为EDIT）
        List<String> editableFields = new ArrayList<>();
        try {
            // 查询用户角色列表
            List<String> userRoles = getUserRoles(userNum);
            if (!userRoles.isEmpty()) {
                // 查询字段权限
                LambdaQueryWrapper<SysFieldPermission> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.in(SysFieldPermission::getRoleCode, userRoles)
                        .eq(SysFieldPermission::getEntityType, entityType)
                        .eq(SysFieldPermission::getPermType, "EDIT")
                        .eq(SysFieldPermission::getStatus, 1)
                        .eq(SysFieldPermission::getIsDeleted, 0);

                List<SysFieldPermission> fieldPermissions = sysFieldPermissionMapper.selectList(queryWrapper);
                editableFields = fieldPermissions.stream()
                        .map(SysFieldPermission::getFieldName)
                        .distinct()
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            // 处理异常，返回空列表
        }

        return editableFields;
    }

    @Override
    public Map<String, Object> filterEditableFields(
            final String userNum,
            final String entityType,
            final Map<String, Object> fieldValues,
            final String currentStatus) {
        // 1. 获取用户可编辑字段列表
        List<String> editableFields = getEditableFields(userNum, entityType, currentStatus);

        // 2. 过滤字段值，仅保留可编辑字段
        Map<String, Object> filteredFields = new HashMap<>();
        for (Map.Entry<String, Object> entry : fieldValues.entrySet()) {
            if (editableFields.contains(entry.getKey())) {
                filteredFields.put(entry.getKey(), entry.getValue());
            }
        }

        return filteredFields;
    }

    @Override
    public List<String> getUserRoles(final String userNum) {
        // 查询用户角色关联表，获取用户角色列表
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserRole::getUserNum, userNum)
                .eq(SysUserRole::getStatus, 1)
                .eq(SysUserRole::getIsDeleted, 0);

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(queryWrapper);

        // 提取角色编码列表
        return userRoles.stream()
                .map(SysUserRole::getRoleCode)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public boolean isLegalApprover(final String userNum, final String taskId) {
        // 简化实现，实际应查询审批任务表和用户角色表
        return true;
    }

    @Override
    public boolean hasApprovalPermission(
            final String userNum,
            final String taskId,
            final String nodeId) {
        // 简化实现，实际应查询审批任务表、节点配置和用户权限表
        return true;
    }
}
