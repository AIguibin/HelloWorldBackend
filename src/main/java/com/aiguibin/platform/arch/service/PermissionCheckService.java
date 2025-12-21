package com.aiguibin.platform.arch.service;

import java.util.List;
import java.util.Map;

/**
 * 权限检查服务接口
 * 负责验证用户权限，查询各种类型权限等
 */
public interface PermissionCheckService {
    /**
     * 验证用户在指定机构下的访问权限
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 包含用户完整权限信息的Map
     */
    Map<String, Object> checkOrgAccess(String userNum, String orgCode);
    
    /**
     * 获取所有权限定义
     * @return 所有权限定义列表
     */
    List<Map<String, Object>> getAllPermissions();
    
    /**
     * 获取用户直接权限
     * @param userNum 用户编号
     * @return 用户直接权限列表
     */
    List<Map<String, Object>> getUserPermissions(String userNum);
    
    /**
     * 获取角色权限
     * @param userRoles 用户角色列表
     * @return 角色权限列表
     */
    List<Map<String, Object>> getRolePermissions(List<Map<String, Object>> userRoles);
    
    /**
     * 获取菜单权限
     * @param userRoles 用户角色列表
     * @return 菜单权限列表
     */
    List<Map<String, Object>> getMenuPermissions(List<Map<String, Object>> userRoles);
    
    /**
     * 获取页面权限（包含按钮权限）
     * @param userRoles 用户角色列表
     * @return 页面权限列表
     */
    List<Map<String, Object>> getPagePermissions(List<Map<String, Object>> userRoles);
    
    /**
     * 获取时间权限
     * @param userRoles 用户角色列表
     * @return 时间权限列表
     */
    List<Map<String, Object>> getTimePermissions(List<Map<String, Object>> userRoles);
    
    /**
     * 获取字段权限
     * @param userRoles 用户角色列表
     * @return 字段权限列表
     */
    List<Map<String, Object>> getFieldPermissions(List<Map<String, Object>> userRoles);
    
    /**
     * 获取API权限
     * @param userRoles 用户角色列表
     * @return API权限列表
     */
    List<Map<String, Object>> getApiPermissions(List<Map<String, Object>> userRoles);
    
    /**
     * 获取业务权限
     * @param userRoles 用户角色列表
     * @return 业务权限列表
     */
    List<Map<String, Object>> getBizPermissions(List<Map<String, Object>> userRoles);
}