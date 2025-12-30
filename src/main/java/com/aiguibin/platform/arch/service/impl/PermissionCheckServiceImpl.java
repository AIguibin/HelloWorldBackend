package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.mapper.*;
import com.aiguibin.platform.arch.service.DataScopeService;
import com.aiguibin.platform.arch.service.PermissionCheckService;
import com.aiguibin.platform.arch.service.UserOrgDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 权限检查服务实现类
 * 负责验证用户权限，查询各种类型权限等
 */
@Service
@Slf4j
public class PermissionCheckServiceImpl implements PermissionCheckService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    
    @Autowired
    private SysRoleOrgMapper sysRoleOrgMapper;
    
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    
    @Autowired
    private SysPermResourceMapper sysPermResourceMapper;
    
    @Autowired
    private MenuMapper menuMapper;
    
    @Autowired
    private SysFieldPermissionMapper sysFieldPermissionMapper;
    
    @Autowired
    private SysApiResourceMapper sysApiResourceMapper;
    
    @Autowired
    private SysBizPermissionMapper sysBizPermissionMapper;
    
    @Autowired
    private UserOrgDeptService userOrgDeptService;
    
    @Autowired
    private DataScopeService dataScopeService;

    @Override
    public Map<String, Object> checkOrgAccess(String userNum, String orgCode) {
        log.info("开始检查用户[{}]在机构[{}]的访问权限", userNum, orgCode);
        try {
            // 1. 查询用户基本信息
            Map<String, Object> userInfo = userMapper.selectUserByUserNum(userNum);
            if (userInfo == null) {
                log.error("用户[{}]不存在", userNum);
                throw new RuntimeException("用户不存在");
            }

            // 2. 查询机构信息
            Map<String, Object> orgInfo = userOrgDeptService.selectOrgByOrgCode(orgCode);
            if (orgInfo == null) {
                log.error("机构[{}]不存在", orgCode);
                throw new RuntimeException("机构不存在");
            }
            
            // 3. 验证用户是否属于该机构
            Map<String, Object> userOrgRelation = userOrgDeptService.selectUserOrgRelation(userNum, orgCode);
            if (userOrgRelation == null) {
                log.error("用户[{}]不属于机构[{}]", userNum, orgCode);
                throw new RuntimeException("用户不属于该机构");
            }
            
            // 4. 查询用户在该机构下的部门信息
            List<Map<String, Object>> userDeptsInOrg = userOrgDeptService.selectAccessibleDeptsByUserNumAndOrgCode(userNum, orgCode);
            if (userDeptsInOrg == null || userDeptsInOrg.isEmpty()) {
                log.error("用户[{}]在机构[{}]下没有可访问的部门信息", userNum, orgCode);
                throw new RuntimeException("用户在该机构下没有可访问的部门");
            }
            
            // 5. 查询用户角色信息
            List<Map<String, Object>> userRoles = sysUserRoleMapper.selectUserRolesWithDataScope(userNum);
            if (userRoles == null || userRoles.isEmpty()) {
                log.error("用户[{}]没有分配任何角色", userNum);
                throw new RuntimeException("用户没有分配任何角色");
            }
            
            // 6. 查询角色的机构数据范围
            List<Map<String, Object>> roleOrgScopes = new ArrayList<>();
            if (!userRoles.isEmpty()) {
                List<String> roleCodes = userRoles.stream()
                        .map(role -> (String) role.get("roleCode"))
                        .collect(Collectors.toList());
                roleOrgScopes = sysRoleOrgMapper.selectRoleOrgScopesByRoleCodes(roleCodes);
                if (roleOrgScopes == null) {
                    log.error("查询角色[{}]的机构数据范围失败", String.join(",", roleCodes));
                    roleOrgScopes = new ArrayList<>();
                }
            }
            
            // 7. 计算用户在该机构下的数据范围（增强版）
            Map<String, Object> dataPermissions = dataScopeService.calculateEnhancedDataScope(userRoles, roleOrgScopes, userNum, orgCode);
            if (dataPermissions == null) {
                log.error("计算用户[{}]在机构[{}]下的数据范围失败", userNum, orgCode);
                throw new RuntimeException("计算数据范围失败");
            }
            
            // 8. 查询用户所有权限定义
            List<Map<String, Object>> allPermissions = getAllPermissions();
            if (allPermissions == null) {
                log.error("查询所有权限定义失败");
                throw new RuntimeException("查询权限定义失败");
            }
            
            // 9. 用户直接权限（当前为空列表，如有直接授权可在此补充）
            List<Map<String, Object>> userPermissions = getUserPermissions(userNum);
            if (userPermissions == null) {
                userPermissions = new ArrayList<>();
            }
            
            // 10. 角色权限
            List<Map<String, Object>> rolePermissions = getRolePermissions(userRoles);
            if (rolePermissions == null) {
                log.error("查询用户[{}]的角色权限失败", userNum);
                throw new RuntimeException("查询角色权限失败");
            }
            
            // 11. 菜单权限
            List<Map<String, Object>> menuPermissions = getMenuPermissions(userRoles);
            if (menuPermissions == null) {
                log.error("查询用户[{}]的菜单权限失败", userNum);
                throw new RuntimeException("查询菜单权限失败");
            }
            
            // 12. 页面权限（包含按钮权限）
            List<Map<String, Object>> pagePermissions = getPagePermissions(userRoles);
            if (pagePermissions == null) {
                log.error("查询用户[{}]的页面权限失败", userNum);
                throw new RuntimeException("查询页面权限失败");
            }
            
            // 13. 时间权限
            List<Map<String, Object>> timePermissions = getTimePermissions(userRoles);
            if (timePermissions == null) {
                timePermissions = new ArrayList<>();
            }
            
            // 14. 字段权限
            List<Map<String, Object>> fieldPermissions = getFieldPermissions(userRoles);
            if (fieldPermissions == null) {
                log.error("查询用户[{}]的字段权限失败", userNum);
                throw new RuntimeException("查询字段权限失败");
            }
            
            // 15. API权限
            List<Map<String, Object>> apiPermissions = getApiPermissions(userRoles);
            if (apiPermissions == null) {
                log.error("查询用户[{}]的API权限失败", userNum);
                throw new RuntimeException("查询API权限失败");
            }
            
            // 16. 业务权限
            List<Map<String, Object>> bizPermissions = getBizPermissions(userRoles);
            if (bizPermissions == null) {
                log.error("查询用户[{}]的业务权限失败", userNum);
                throw new RuntimeException("查询业务权限失败");
            }
            
            // 17. 构建返回数据
            Map<String, Object> result = new HashMap<>();
            
            // 用户基本信息
            result.put("user", userInfo);
            
            // 当前机构部门信息
            Map<String, Object> currentOrgDepts = new HashMap<>();
            currentOrgDepts.put("orgCode", orgInfo.get("orgCode"));
            currentOrgDepts.put("orgName", orgInfo.get("orgName"));
            currentOrgDepts.put("parentOrgCode", orgInfo.get("parentOrgCode"));
            currentOrgDepts.put("level", orgInfo.get("level"));
            currentOrgDepts.put("status", orgInfo.get("status"));
            currentOrgDepts.put("depts", userDeptsInOrg);
            result.put("currentOrgDepts", currentOrgDepts);
            
            // 所有权限
            result.put("allPermissions", allPermissions);
            
            // 用户直接权限
            result.put("userPermissions", userPermissions);
            
            // 角色权限
            result.put("rolePermissions", rolePermissions);
            
            // 菜单权限
            result.put("menuPermissions", menuPermissions);
            
            // 页面权限
            result.put("pagePermissions", pagePermissions);
            
            // 数据权限
            result.put("dataPermissions", dataPermissions);
            
            // 字段权限
            result.put("fieldPermissions", fieldPermissions);
            
            // API权限
            result.put("apiPermissions", apiPermissions);
            
            // 业务权限
            result.put("bizPermissions", bizPermissions);
            
            // 时间权限
            result.put("timePermissions", timePermissions);
            
            log.info("用户[{}]在机构[{}]的访问权限检查完成", userNum, orgCode);
            return result;
        } catch (Exception e) {
            log.error("检查用户[{}]在机构[{}]的访问权限失败", userNum, orgCode, e);
            throw new RuntimeException("检查访问权限失败");
        }
    }
    
    @Override
    public List<Map<String, Object>> getAllPermissions() {
        List<Map<String, Object>> permissions = sysPermissionMapper.selectAll();
        return permissions.stream()
            .map(perm -> {
                Map<String, Object> permInfo = new HashMap<>();
                permInfo.put("permCode", perm.get("permCode"));
                permInfo.put("permName", perm.get("permName"));
                permInfo.put("permKey", perm.get("permKey"));
                permInfo.put("permType", perm.get("permType"));
                permInfo.put("actionType", perm.get("actionType"));
                permInfo.put("effectType", perm.get("effectType"));
                permInfo.put("status", perm.get("status"));
                permInfo.put("description", perm.get("description"));
                return permInfo;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getUserPermissions(String userNum) {
        // 当前为空列表，如有直接授权可在此补充
        return new ArrayList<>();
    }
    
    @Override
    public List<Map<String, Object>> getRolePermissions(List<Map<String, Object>> userRoles) {
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> roleCodes = userRoles.stream()
            .map(role -> (String) role.get("roleCode"))
            .collect(Collectors.toList());
        
        List<Map<String, Object>> rolePermissions = sysPermissionMapper.selectPermissionsByRoleCodes(roleCodes);
        
        return rolePermissions.stream()
            .map(rp -> {
                Map<String, Object> perm = new HashMap<>();
                perm.put("roleCode", rp.get("roleCode"));
                perm.put("permCode", rp.get("permCode"));
                perm.put("permName", rp.get("permName"));
                perm.put("authType", rp.get("authType"));
                perm.put("effectiveStart", rp.get("effectiveStart"));
                perm.put("effectiveEnd", rp.get("effectiveEnd"));
                return perm;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getMenuPermissions(List<Map<String, Object>> userRoles) {
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> roleCodes = userRoles.stream()
            .map(role -> (String) role.get("roleCode"))
            .collect(Collectors.toList());
        
        List<Map<String, Object>> menuResources = sysPermResourceMapper.selectResourcesByRoleCodes(roleCodes, "MENU");
        
        return menuResources.stream()
            .map(res -> {
                Map<String, Object> menu = new HashMap<>();
                menu.put("resourceKey", res.get("resourceKey"));
                menu.put("resourceType", res.get("resourceType"));
                menu.put("permCode", res.get("permCode"));
                return menu;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getPagePermissions(List<Map<String, Object>> userRoles) {
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> roleCodes = userRoles.stream()
            .map(role -> (String) role.get("roleCode"))
            .collect(Collectors.toList());
        
        // 查询角色拥有的菜单权限
        List<Map<String, Object>> menuResources = sysPermResourceMapper.selectResourcesByRoleCodes(roleCodes, "MENU");
        
        // 提取有权限的菜单资源标识
        Set<String> allowedResourceKeys = menuResources.stream()
            .map(res -> (String) res.get("resourceKey"))
            .collect(Collectors.toSet());
        
        // 获取所有页面菜单（menu_type = 'P'）
        List<Map<String, Object>> pageMenus = menuMapper.selectByMenuType("P");
        
        // 组装页面权限结构
        return pageMenus.stream()
            .filter(menu -> allowedResourceKeys.contains(menu.get("resourceKey")))
            .map(pageMenu -> {
                Map<String, Object> pagePerm = new HashMap<>();
                pagePerm.put("menuCode", pageMenu.get("menuCode"));
                pagePerm.put("menuName", pageMenu.get("menuName"));
                pagePerm.put("menuType", pageMenu.get("menuType"));
                pagePerm.put("path", pageMenu.get("path"));
                pagePerm.put("component", pageMenu.get("component"));
                pagePerm.put("resourceKey", pageMenu.get("resourceKey"));
                
                // 获取该页面的按钮权限
                String parentMenuCode = (String) pageMenu.get("menuCode");
                List<Map<String, Object>> buttonMenus = menuMapper.selectButtonsByParentCode(parentMenuCode);
                
                List<Map<String, Object>> btnsPermissions = buttonMenus.stream()
                    .filter(btn -> allowedResourceKeys.contains(btn.get("resourceKey")))
                    .map(btn -> {
                        Map<String, Object> btnPerm = new HashMap<>();
                        btnPerm.put("menuCode", btn.get("menuCode"));
                        btnPerm.put("menuName", btn.get("menuName"));
                        btnPerm.put("menuType", btn.get("menuType"));
                        btnPerm.put("resourceKey", btn.get("resourceKey"));
                        return btnPerm;
                    })
                    .collect(Collectors.toList());
                
                pagePerm.put("btnsPermissions", btnsPermissions);
                return pagePerm;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getTimePermissions(List<Map<String, Object>> userRoles) {
        // 当前时间权限功能未实现，返回空列表
        return new ArrayList<>();
    }
    
    @Override
    public List<Map<String, Object>> getFieldPermissions(List<Map<String, Object>> userRoles) {
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> roleCodes = userRoles.stream()
            .map(role -> (String) role.get("roleCode"))
            .collect(Collectors.toList());
        
        // 查询角色拥有的字段权限
        List<Map<String, Object>> fieldResources = sysPermResourceMapper.selectResourcesByRoleCodes(roleCodes, "FIELD");
        
        List<String> permCodes = fieldResources.stream()
            .map(res -> (String) res.get("permCode"))
            .distinct()
            .collect(Collectors.toList());
        
        if (permCodes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询字段权限详情
        List<Map<String, Object>> fieldPermissions = sysFieldPermissionMapper.selectByPermCodes(permCodes);
        
        return fieldPermissions.stream()
            .map(fp -> {
                Map<String, Object> fieldPerm = new HashMap<>();
                fieldPerm.put("permCode", fp.get("permCode"));
                fieldPerm.put("fieldCode", fp.get("fieldCode"));
                fieldPerm.put("entityType", fp.get("entityType"));
                fieldPerm.put("fieldName", fp.get("fieldName"));
                fieldPerm.put("fieldAlias", fp.get("fieldAlias"));
                fieldPerm.put("fieldType", fp.get("fieldType"));
                fieldPerm.put("status", fp.get("status"));
                return fieldPerm;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getApiPermissions(List<Map<String, Object>> userRoles) {
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> roleCodes = userRoles.stream()
            .map(role -> (String) role.get("roleCode"))
            .collect(Collectors.toList());
        
        // 查询角色拥有的API权限
        List<Map<String, Object>> apiResources = sysPermResourceMapper.selectResourcesByRoleCodes(roleCodes, "API");
        
        // 提取有权限的API资源标识
        Set<String> allowedResourceKeys = apiResources.stream()
            .map(res -> (String) res.get("resourceKey"))
            .collect(Collectors.toSet());
        
        // 查询所有API资源
        List<Map<String, Object>> allApis = sysApiResourceMapper.selectAll();
        
        // 过滤用户有权限的API
        return allApis.stream()
            .filter(api -> allowedResourceKeys.contains(api.get("resourceKey")))
            .map(api -> {
                Map<String, Object> apiPerm = new HashMap<>();
                apiPerm.put("apiCode", api.get("apiCode"));
                apiPerm.put("apiName", api.get("apiName"));
                apiPerm.put("apiPath", api.get("apiPath"));
                apiPerm.put("httpMethod", api.get("httpMethod"));
                apiPerm.put("resourceKey", api.get("resourceKey"));
                apiPerm.put("serviceName", api.get("serviceName"));
                apiPerm.put("needAuth", api.get("needAuth"));
                return apiPerm;
            })
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getBizPermissions(List<Map<String, Object>> userRoles) {
        if (userRoles.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> roleCodes = userRoles.stream()
            .map(role -> (String) role.get("roleCode"))
            .collect(Collectors.toList());
        
        // 查询角色拥有的业务权限
        List<Map<String, Object>> bizResources = sysPermResourceMapper.selectResourcesByRoleCodes(roleCodes, "BUSINESS");
        
        List<String> permCodes = bizResources.stream()
            .map(res -> (String) res.get("permCode"))
            .distinct()
            .collect(Collectors.toList());
        
        if (permCodes.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 查询业务权限详情
        List<Map<String, Object>> bizPermissions = sysBizPermissionMapper.selectByPermCodes(permCodes);
        
        return bizPermissions.stream()
            .map(bp -> {
                Map<String, Object> bizPerm = new HashMap<>();
                bizPerm.put("permCode", bp.get("permCode"));
                bizPerm.put("businessName", bp.get("businessName"));
                bizPerm.put("businessType", bp.get("businessType"));
                bizPerm.put("ruleEngine", bp.get("ruleEngine"));
                bizPerm.put("status", bp.get("status"));
                return bizPerm;
            })
            .collect(Collectors.toList());
    }
}