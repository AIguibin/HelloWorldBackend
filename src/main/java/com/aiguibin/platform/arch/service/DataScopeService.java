package com.aiguibin.platform.arch.service;

import java.util.List;
import java.util.Map;

/**
 * 数据范围服务接口
 * 负责计算用户数据范围，生成SQL条件等
 */
public interface DataScopeService {
    /**
     * 增强的数据范围计算（包含默认、机构、实体三层）
     * @param userRoles 用户角色列表
     * @param roleOrgScopes 角色机构范围列表
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 数据权限Map
     */
    Map<String, Object> calculateEnhancedDataScope(List<Map<String, Object>> userRoles, 
                                                  List<Map<String, Object>> roleOrgScopes, 
                                                  String userNum, 
                                                  String orgCode);
    
    /**
     * 计算默认数据范围（来自sys_role.data_scope_type）
     * @param userRoles 用户角色列表
     * @return 默认数据范围Map
     */
    Map<String, Object> calculateDefaultDataScope(List<Map<String, Object>> userRoles);
    
    /**
     * 计算机构授权范围（来自sys_role_org）
     * @param roleOrgScopes 角色机构范围列表
     * @param orgCode 机构编码
     * @return 机构授权范围Map
     */
    Map<String, Object> calculateOrgAuthorityScope(List<Map<String, Object>> roleOrgScopes, String orgCode);
    
    /**
     * 查询实体数据范围（来自sys_data_permission）
     * @param userRoles 用户角色列表
     * @param userNum 用户编号
     * @return 实体数据范围列表
     */
    List<Map<String, Object>> getEntityDataScopes(List<Map<String, Object>> userRoles, String userNum);
    
    /**
     * 计算实际有效数据范围（优先级：实体规则 > 机构授权 > 角色默认）
     * @param defaultDataScope 默认数据范围
     * @param orgAuthorityScope 机构授权范围
     * @param entityDataScopes 实体数据范围列表
     * @param orgCode 机构编码
     * @param userNum 用户编号
     * @return 实际有效数据范围Map
     */
    Map<String, Object> calculateEffectiveScope(Map<String, Object> defaultDataScope, 
                                              Map<String, Object> orgAuthorityScope, 
                                              List<Map<String, Object>> entityDataScopes, 
                                              String orgCode, 
                                              String userNum);
    
    /**
     * 构建权限计算路径（便于调试和理解）
     * @param defaultDataScope 默认数据范围
     * @param orgAuthorityScope 机构授权范围
     * @param entityDataScopes 实体数据范围列表
     * @param effectiveScope 实际有效数据范围
     * @return 权限计算路径列表
     */
    List<Map<String, Object>> buildScopePath(Map<String, Object> defaultDataScope, 
                                            Map<String, Object> orgAuthorityScope, 
                                            List<Map<String, Object>> entityDataScopes, 
                                            Map<String, Object> effectiveScope);
    
    /**
     * 生成SQL条件
     * @param scopeType 数据范围类型
     * @param orgCode 机构编码
     * @param userNum 用户编号
     * @return SQL条件Map
     */
    Map<String, Object> generateSQLCondition(Integer scopeType, String orgCode, String userNum);
    
    /**
     * 获取用户在当前机构的主部门
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 主部门编码
     */
    String getUserPrimaryDept(String userNum, String orgCode);
    
    /**
     * 获取数据范围标签
     * @param scopeType 数据范围类型
     * @return 数据范围标签
     */
    String getDataScopeLabel(Integer scopeType);
    
    /**
     * 获取机构范围标签
     * @param orgRangeType 机构范围类型
     * @return 机构范围标签
     */
    String getOrgRangeLabel(Integer orgRangeType);
    
    /**
     * 获取权限类型标签
     * @param permType 权限类型
     * @return 权限类型标签
     */
    String getPermTypeLabel(Integer permType);
}