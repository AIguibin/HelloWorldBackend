package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.mapper.SysDataPermissionMapper;
import com.aiguibin.platform.arch.mapper.SysPermResourceMapper;
import com.aiguibin.platform.arch.service.DataScopeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据范围服务实现类
 * 负责计算用户数据范围，生成SQL条件等
 */
@Service
@Slf4j
public class DataScopeServiceImpl implements DataScopeService {
    // 数据范围类型常量
    private static final int DATA_SCOPE_ALL = 1; // 全部数据
    private static final int DATA_SCOPE_ORG_WITH_CHILDREN = 2; // 本机构及下级
    private static final int DATA_SCOPE_ORG = 3; // 本机构
    private static final int DATA_SCOPE_DEPT_WITH_CHILDREN = 4; // 本部门及下级
    private static final int DATA_SCOPE_DEPT = 5; // 本部门
    private static final int DATA_SCOPE_SELF = 6; // 本人数据
    private static final int DATA_SCOPE_CUSTOM = 7; // 自定义数据
    
    // 机构范围常量
    private static final String ORG_SCOPE_ALL = "ALL"; // 全部机构特殊编码

    @Autowired
    private SysPermResourceMapper sysPermResourceMapper;
    
    @Autowired
    private SysDataPermissionMapper sysDataPermissionMapper;

    @Override
    public Map<String, Object> calculateEnhancedDataScope(List<Map<String, Object>> userRoles,
                                                         List<Map<String, Object>> roleOrgScopes,
                                                         String userNum,
                                                         String orgCode) {
        // 验证输入参数
        if (userNum == null || userNum.isEmpty()) {
            log.error("用户编码参数为空");
            throw new IllegalArgumentException("用户编码参数不能为空");
        }
        
        if (orgCode == null || orgCode.isEmpty()) {
            log.error("机构编码参数为空");
            throw new IllegalArgumentException("机构编码参数不能为空");
        }
        
        if (userRoles == null) {
            log.warn("用户角色列表为null，使用默认空集合");
            userRoles = new ArrayList<>();
        }
        
        if (roleOrgScopes == null) {
            log.warn("角色机构范围列表为null，使用默认空集合");
            roleOrgScopes = new ArrayList<>();
        }
        
        Map<String, Object> dataPermissions = new HashMap<>();
        log.info("开始计算用户[{}]在机构[{}]的数据范围", userNum, orgCode);
        
        // 1. 计算默认数据范围（来自sys_role.data_scope_type）
        Map<String, Object> defaultDataScope = calculateDefaultDataScope(userRoles);
        dataPermissions.put("defaultDataScope", defaultDataScope);
        
        // 2. 计算机构授权范围（来自sys_role_org）
        Map<String, Object> orgAuthorityScope = calculateOrgAuthorityScope(roleOrgScopes, orgCode);
        dataPermissions.put("orgAuthorityScope", orgAuthorityScope);
        
        // 3. 查询实体数据范围（来自sys_data_permission）
        List<Map<String, Object>> entityDataScopes = getEntityDataScopes(userRoles, userNum);
        dataPermissions.put("entityDataScopes", entityDataScopes);
        
        // 4. 计算实际有效数据范围（优先级：实体规则 > 机构授权 > 角色默认）
        Map<String, Object> effectiveScope = calculateEffectiveScope(
            defaultDataScope, orgAuthorityScope, entityDataScopes, orgCode, userNum);
        dataPermissions.put("effectiveScope", effectiveScope);
        
        // 5. 权限计算路径（便于调试和理解）
        dataPermissions.put("scopePath", buildScopePath(defaultDataScope, orgAuthorityScope, entityDataScopes, effectiveScope));
        
        return dataPermissions;
    }
    
    @Override
    public Map<String, Object> calculateDefaultDataScope(List<Map<String, Object>> userRoles) {
        Map<String, Object> defaultScope = new HashMap<>();
        
        // 验证输入参数
        if (userRoles == null || userRoles.isEmpty()) {
            log.info("用户角色列表为空，使用默认数据范围：本人数据");
            defaultScope.put("scopeType", DATA_SCOPE_SELF);
            defaultScope.put("scopeTypeLabel", "本人");
            defaultScope.put("source", "系统默认（无角色）");
            return defaultScope;
        }
        
        // 取最宽的权限（数值最小）
        Integer minScopeType = DATA_SCOPE_ALL;
        List<String> contributingRoles = new ArrayList<>();
        
        for (Map<String, Object> role : userRoles) {
            Integer roleDataScope = (Integer) role.get("dataScopeType");
            if (roleDataScope != null && roleDataScope < minScopeType) {
                minScopeType = roleDataScope;
            }
            contributingRoles.add((String) role.get("roleCode"));
        }
        
        defaultScope.put("scopeType", minScopeType);
        defaultScope.put("scopeTypeLabel", getDataScopeLabel(minScopeType));
        defaultScope.put("source", "sys_role.data_scope_type");
        defaultScope.put("contributingRoles", contributingRoles);
        defaultScope.put("calculationRule", "取所有角色中data_scope_type的最小值（数值越小权限越宽）");
        
        return defaultScope;
    }
    
    @Override
    public Map<String, Object> calculateOrgAuthorityScope(List<Map<String, Object>> roleOrgScopes, String orgCode) {
        // 验证输入参数
        if (orgCode == null || orgCode.isEmpty()) {
            log.error("机构编码参数为空");
            throw new IllegalArgumentException("机构编码参数不能为空");
        }
        
        Map<String, Object> orgScope = new HashMap<>();
        
        orgScope.put("orgCode", orgCode);
        orgScope.put("hasAccess", false);
        orgScope.put("source", "sys_role_org");
        
        if (roleOrgScopes == null || roleOrgScopes.isEmpty()) {
            log.info("角色机构范围列表为空，机构[{}]默认无访问权限", orgCode);
            return orgScope;
        }
        
        // 查找当前机构的授权记录
        List<Map<String, Object>> currentOrgScopes = roleOrgScopes.stream()
            .filter(scope -> orgCode.equals(scope.get("orgCode")))
            .collect(Collectors.toList());
        
        if (!currentOrgScopes.isEmpty()) {
            // 取权限最高的记录（perm_type最小）
            // 取权限最高的记录（perm_type最小，null值视为最大权限）
            Map<String, Object> bestScope = currentOrgScopes.stream()
                .min(Comparator.comparing(scope -> (Integer) scope.get("permType"), 
                    Comparator.nullsLast(Comparator.naturalOrder())))
                .orElse(currentOrgScopes.get(0));
            
            orgScope.put("hasAccess", true);
            orgScope.put("orgRangeType", bestScope.get("orgRangeType"));
            orgScope.put("permType", bestScope.get("permType"));
            orgScope.put("orgRangeLabel", getOrgRangeLabel((Integer) bestScope.get("orgRangeType")));
            orgScope.put("permTypeLabel", getPermTypeLabel((Integer) bestScope.get("permType")));
            orgScope.put("effectiveStart", bestScope.get("effectiveStart"));
            orgScope.put("effectiveEnd", bestScope.get("effectiveEnd"));
        } else {
            // 检查是否有全部机构权限
            boolean hasAllOrgAccess = roleOrgScopes.stream()
                .anyMatch(scope -> ORG_SCOPE_ALL.equals(scope.get("orgCode")));
            
            if (hasAllOrgAccess) {
                // 取全部机构的最高权限配置（perm_type最小，null值视为最大权限）
                Map<String, Object> allOrgScope = roleOrgScopes.stream()
                    .filter(scope -> ORG_SCOPE_ALL.equals(scope.get("orgCode")))
                    .min(Comparator.comparing(scope -> (Integer) scope.get("permType"),
                        Comparator.nullsLast(Comparator.naturalOrder())))
                    .orElse(null);
                
                if (allOrgScope != null) {
                    orgScope.put("hasAccess", true);
                    orgScope.put("orgRangeType", allOrgScope.get("orgRangeType"));
                    orgScope.put("permType", allOrgScope.get("permType"));
                    orgScope.put("orgRangeLabel", getOrgRangeLabel((Integer) allOrgScope.get("orgRangeType")));
                    orgScope.put("permTypeLabel", getPermTypeLabel((Integer) allOrgScope.get("permType")));
                    orgScope.put("effectiveStart", allOrgScope.get("effectiveStart"));
                    orgScope.put("effectiveEnd", allOrgScope.get("effectiveEnd"));
                    orgScope.put("isAllOrg", true);
                }
            }
        }
        
        return orgScope;
    }
    
    @Override
    public List<Map<String, Object>> getEntityDataScopes(List<Map<String, Object>> userRoles, String userNum) {
        if (userRoles == null || userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 提取角色编码
        List<String> roleCodes = userRoles.stream()
            .map(role -> (String) role.get("roleCode"))
            .collect(Collectors.toList());
        
        // 查询角色拥有的数据权限（通过sys_perm_resource关联）
        List<Map<String, Object>> dataResources = sysPermResourceMapper.selectResourcesByRoleCodes(roleCodes, "DATA");
        
        // 验证数据资源查询结果
        if (dataResources==null) {
            log.warn("用户[{}]的数据资源查询结果为空，返回默认空集合", userNum);
            return new ArrayList<>();
        }
        
        // 提取权限编码
        List<String> permCodes = dataResources.stream()
            .map(res -> (String) res.get("permCode"))
            .distinct()
            .collect(Collectors.toList());
        
        // 查询数据权限详情
        List<Map<String, Object>> entityScopes = sysDataPermissionMapper.selectByPermCodes(permCodes);
        
        // 验证数据权限查询结果
        if (entityScopes == null) {
            log.warn("权限编码[{}]对应的数据权限查询结果为null，返回默认空集合", permCodes);
            return new ArrayList<>();
        }
        
        if (entityScopes.isEmpty()) {
            log.debug("权限编码[{}]对应的数据权限查询结果为空集合", permCodes);
            return new ArrayList<>();
        }
        
        // 转换为标准格式
        return entityScopes.stream()
            .map(scope -> {
                Map<String, Object> entityScope = new HashMap<>();
                
                // 验证单个权限记录的完整性
                if (scope.get("permCode") == null) {
                    log.warn("数据权限记录缺少permCode字段，忽略该记录");
                    return null;
                }
                
                if (scope.get("scopeType") == null) {
                    log.warn("数据权限记录[{}]缺少scopeType字段，忽略该记录", scope.get("permCode"));
                    return null;
                }
                
                // 验证scopeType类型安全性
                Integer scopeType = null;
                try {
                    scopeType = (Integer) scope.get("scopeType");
                } catch (ClassCastException e) {
                    log.error("数据权限记录[{}]的scopeType字段类型错误，应为Integer，实际为{}", 
                             scope.get("permCode"), scope.get("scopeType").getClass().getName(), e);
                    return null;
                }
                
                // 构建实体数据范围对象
                entityScope.put("permCode", scope.get("permCode"));
                entityScope.put("dataName", scope.get("dataName"));
                entityScope.put("entityType", scope.get("entityType"));
                entityScope.put("scopeType", scopeType);
                entityScope.put("scopeTypeLabel", getDataScopeLabel(scopeType));
                entityScope.put("includeChildren", scope.get("includeChildren"));
                entityScope.put("ruleType", scope.get("ruleType"));
                entityScope.put("rulePriority", scope.get("rulePriority"));
                entityScope.put("isGlobal", scope.get("isGlobal"));
                entityScope.put("status", scope.get("status"));
                entityScope.put("source", "sys_data_permission");
                
                log.debug("成功转换数据权限记录[{}]，scopeType: {}", scope.get("permCode"), scopeType);
                return entityScope;
            })
            .filter(Objects::nonNull) // 过滤掉无效记录
            .collect(Collectors.toList());
    }
    
    @Override
    public Map<String, Object> calculateEffectiveScope(Map<String, Object> defaultDataScope,
                                                      Map<String, Object> orgAuthorityScope,
                                                      List<Map<String, Object>> entityDataScopes,
                                                      String orgCode,
                                                      String userNum) {
        // 验证输入参数
        if (defaultDataScope == null) {
            log.error("默认数据范围参数为null，orgCode: {}, userNum: {}", orgCode, userNum);
            throw new IllegalArgumentException("默认数据范围参数不能为空");
        }
        
        if (orgAuthorityScope == null) {
            log.error("机构授权范围参数为null，orgCode: {}, userNum: {}", orgCode, userNum);
            throw new IllegalArgumentException("机构授权范围参数不能为空");
        }
        
        if (entityDataScopes == null) {
            log.warn("实体数据范围参数为null，使用默认空集合，orgCode: {}, userNum: {}", orgCode, userNum);
            entityDataScopes = new ArrayList<>();
        }
        
        if (orgCode == null || orgCode.isEmpty()) {
            log.error("机构编码参数为空，userNum: {}", userNum);
            throw new IllegalArgumentException("机构编码参数不能为空");
        }
        
        if (userNum == null || userNum.isEmpty()) {
            log.error("用户编码参数为空，orgCode: {}", orgCode);
            throw new IllegalArgumentException("用户编码参数不能为空");
        }
        
        Map<String, Object> effectiveScope = new HashMap<>();
        List<String> appliedRules = new ArrayList<>();
        
        Integer finalScopeType = null;
        String calculationLogic = "";
        
        // 1. 如果没有机构访问权限，只能访问本人数据
        if (!(Boolean) orgAuthorityScope.getOrDefault("hasAccess", false)) {
            finalScopeType = DATA_SCOPE_SELF;
            calculationLogic = "无机构访问权限 → 本人数据";
            appliedRules.add("无机构访问权限，使用最小权限");
        }
        // 2. 优先使用实体数据范围（如果有）
        else if (!entityDataScopes.isEmpty()) {
            // 按优先级排序（数字越小优先级越高）
            List<Map<String, Object>> sortedScopes = entityDataScopes.stream()
                .sorted(Comparator.comparing(scope -> (Integer) scope.get("rulePriority")))
                .collect(Collectors.toList());

            // 取优先级最高的实体范围
            Map<String, Object> highestPriorityScope = sortedScopes.get(0);
            Integer entityScopeType = (Integer) highestPriorityScope.get("scopeType");
            // 确保实体范围类型不为空，为空时使用本人数据范围
            finalScopeType = entityScopeType != null ? entityScopeType : DATA_SCOPE_SELF;
            Object rulePriority = highestPriorityScope.get("rulePriority");
            Object dataName = highestPriorityScope.get("dataName");
            calculationLogic = "实体规则(优先级" + (rulePriority != null ? rulePriority : "未知") + ") → " +
                              getDataScopeLabel(finalScopeType);
            appliedRules.add("应用实体规则: " + (dataName != null ? dataName : "未知实体"));     
        }
        // 3. 其次使用机构授权范围（经过第1步检查，hasAccess必定为true）
        else {
            Integer orgRangeType = (Integer) orgAuthorityScope.get("orgRangeType");        
            // 确保机构范围类型不为空，默认为1（仅本机构）
            orgRangeType = orgRangeType != null ? orgRangeType : 1;

            if (orgRangeType == 2) { // 包含下级机构
                finalScopeType = DATA_SCOPE_ORG_WITH_CHILDREN;
                calculationLogic = "机构授权(包含下级) → 本机构及下级";
            } else {
                finalScopeType = DATA_SCOPE_ORG;
                calculationLogic = "机构授权(仅本机构) → 本机构";
            }

            appliedRules.add("应用机构授权规则");
        }
        
        // 生成SQL条件
        Map<String, Object> sqlCondition = generateSQLCondition(finalScopeType, orgCode, userNum);
        
        effectiveScope.put("scopeType", finalScopeType);
        effectiveScope.put("scopeTypeLabel", getDataScopeLabel(finalScopeType));
        effectiveScope.put("calculationLogic", calculationLogic);
        effectiveScope.put("appliedRules", appliedRules);
        effectiveScope.put("sqlCondition", sqlCondition);
        effectiveScope.put("priorityOrder", "实体规则 > 机构授权 > 角色默认");
        
        return effectiveScope;
    }
    
    @Override
    public List<Map<String, Object>> buildScopePath(Map<String, Object> defaultDataScope,
                                            Map<String, Object> orgAuthorityScope,
                                            List<Map<String, Object>> entityDataScopes,
                                            Map<String, Object> effectiveScope) {
        // 验证输入参数
        if (defaultDataScope == null) {
            log.error("默认数据范围参数为null");
            throw new IllegalArgumentException("默认数据范围参数不能为空");
        }
        
        if (orgAuthorityScope == null) {
            log.error("机构授权范围参数为null");
            throw new IllegalArgumentException("机构授权范围参数不能为空");
        }
        
        if (effectiveScope == null) {
            log.error("有效数据范围参数为null");
            throw new IllegalArgumentException("有效数据范围参数不能为空");
        }
        
        if (entityDataScopes == null) {
            log.warn("实体数据范围参数为null，使用默认空集合");
            entityDataScopes = new ArrayList<>();
        }
        
        List<Map<String, Object>> path = new ArrayList<>();
        
        // 角色默认范围节点
        Map<String, Object> defaultNode = new HashMap<>();
        defaultNode.put("level", "default");
        defaultNode.put("source", "sys_role");
        defaultNode.put("scopeType", defaultDataScope.get("scopeType"));
        defaultNode.put("scopeLabel", defaultDataScope.get("scopeTypeLabel"));
        defaultNode.put("applied", effectiveScope.get("calculationLogic").toString().contains("角色默认"));
        path.add(defaultNode);
        
        // 机构授权节点
        Map<String, Object> orgNode = new HashMap<>();
        orgNode.put("level", "org");
        orgNode.put("source", "sys_role_org");
        orgNode.put("hasAccess", orgAuthorityScope.get("hasAccess"));
        if ((Boolean) orgAuthorityScope.get("hasAccess")) {
            orgNode.put("rangeType", orgAuthorityScope.get("orgRangeType"));
            orgNode.put("rangeLabel", orgAuthorityScope.get("orgRangeLabel"));
            orgNode.put("permType", orgAuthorityScope.get("permTypeLabel"));
        }
        orgNode.put("applied", effectiveScope.get("calculationLogic").toString().contains("机构授权"));
        path.add(orgNode);
        
        // 实体规则节点
        if (entityDataScopes != null && !entityDataScopes.isEmpty()) {
            for (Map<String, Object> entityScope : entityDataScopes) {
                Map<String, Object> entityNode = new HashMap<>();
                entityNode.put("level", "entity");
                entityNode.put("source", "sys_data_permission");
                entityNode.put("entityType", entityScope.get("entityType"));
                entityNode.put("scopeType", entityScope.get("scopeType"));
                entityNode.put("scopeLabel", entityScope.get("scopeTypeLabel"));
                entityNode.put("priority", entityScope.get("rulePriority"));
                entityNode.put("applied", effectiveScope.get("appliedRules").toString()
                    .contains(entityScope.get("dataName").toString()));
                path.add(entityNode);
            }
        }
        
        // 最终有效范围节点
        Map<String, Object> finalNode = new HashMap<>();
        finalNode.put("level", "effective");
        finalNode.put("scopeType", effectiveScope.get("scopeType"));
        finalNode.put("scopeLabel", effectiveScope.get("scopeTypeLabel"));
        finalNode.put("calculationLogic", effectiveScope.get("calculationLogic"));
        path.add(finalNode);
        
        return path;
    }
    
    @Override
    public Map<String, Object> generateSQLCondition(Integer scopeType, String orgCode, String userNum) {
        Map<String, Object> condition = new HashMap<>();
        Map<String, Object> parameters = new HashMap<>();
        String whereClause = "";
        
        switch (scopeType) {
            case DATA_SCOPE_ALL:
                whereClause = "1 = 1";
                break;
            case DATA_SCOPE_ORG_WITH_CHILDREN:
                whereClause = "org_code IN (SELECT org_code FROM sys_org WHERE org_code = :orgCode OR parent_org_code = :orgCode)";
                parameters.put("orgCode", orgCode);
                break;
            case DATA_SCOPE_ORG:
                whereClause = "org_code = :orgCode";
                parameters.put("orgCode", orgCode);
                break;
            case DATA_SCOPE_DEPT_WITH_CHILDREN:
                whereClause = "dept_code IN (SELECT dept_code FROM sys_dept WHERE dept_code = :deptCode OR parent_dept_code = :deptCode)";
                // 这里需要查询用户在当前机构的主部门
                parameters.put("deptCode", getUserPrimaryDept(userNum, orgCode));
                break;
            case DATA_SCOPE_DEPT:
                whereClause = "dept_code = :deptCode";
                parameters.put("deptCode", getUserPrimaryDept(userNum, orgCode));
                break;
            case DATA_SCOPE_SELF:
            default:
                whereClause = "created_by = :userNum";
                parameters.put("userNum", userNum);
                break;
        }
        
        condition.put("whereClause", whereClause);
        condition.put("parameters", parameters);
        condition.put("generatedAt", new Date());
        
        return condition;
    }
    
    @Override
    public String getUserPrimaryDept(String userNum, String orgCode) {
        // 这里简化处理，实际需要查询数据库
        // 可以调用sysUserDeptMapper.selectPrimaryDeptByUserNumAndOrgCode(userNum, orgCode)
        return null;
    }
    
    @Override
    public String getDataScopeLabel(Integer scopeType) {
        if (scopeType == null) return "未知";
        
        switch (scopeType) {
            case DATA_SCOPE_ALL: return "全部数据";
            case DATA_SCOPE_ORG_WITH_CHILDREN: return "本机构及下级";
            case DATA_SCOPE_ORG: return "本机构";
            case DATA_SCOPE_DEPT_WITH_CHILDREN: return "本部门及下级";
            case DATA_SCOPE_DEPT: return "本部门";
            case DATA_SCOPE_SELF: return "本人";
            case DATA_SCOPE_CUSTOM: return "自定义";
            default: return "未知";
        }
    }
    
    @Override
    public String getOrgRangeLabel(Integer orgRangeType) {
        if (orgRangeType == null) return "未知";
        return orgRangeType == 1 ? "本机构" : "包含下级机构";
    }
    
    @Override
    public String getPermTypeLabel(Integer permType) {
        if (permType == null) return "未知";
        
        switch (permType) {
            case 1: return "管理";
            case 2: return "查看";
            case 3: return "操作";
            default: return "未知";
        }
    }
}