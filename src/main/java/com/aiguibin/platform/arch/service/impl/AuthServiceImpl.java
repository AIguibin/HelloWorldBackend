package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.mapper.SysOrgMapper;
import com.aiguibin.platform.arch.mapper.SysUserDeptMapper;
import com.aiguibin.platform.arch.mapper.SysUserOrgMapper;
import com.aiguibin.platform.arch.mapper.SysUserRoleMapper;
import com.aiguibin.platform.arch.mapper.SysRoleOrgMapper;
import com.aiguibin.platform.arch.mapper.UserMapper;
import com.aiguibin.platform.arch.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.aiguibin.platform.arch.entity.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    // 数据范围类型常量
    private static final int DATA_SCOPE_ALL = 1; // 全部数据
    private static final int DATA_SCOPE_ORG = 2; // 本机构数据
    private static final int DATA_SCOPE_DEPT = 3; // 本部门数据
    private static final int DATA_SCOPE_SELF = 4; // 本人数据
    private static final int DATA_SCOPE_CUSTOM = 5; // 自定义数据
    
    // 机构范围常量
    private static final String ORG_SCOPE_ALL = "1"; // 全部机构标识
    
    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, String> csrfStore = new ConcurrentHashMap<>();

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SysUserOrgMapper sysUserOrgMapper;
    
    @Autowired
    private SysUserDeptMapper sysUserDeptMapper;
    
    @Autowired
    private SysOrgMapper sysOrgMapper;
    
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;
    
    @Autowired
    private SysRoleOrgMapper sysRoleOrgMapper;

    @Override
    public String issueToken(String userNum) {
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        tokenStore.put(token, userNum);
        String csrfToken = UUID.randomUUID().toString().replaceAll("-", "");
        csrfStore.put(token, csrfToken);
        return token;
    }

    @Override
    public String getUserNumByToken(String token) {
        return tokenStore.get(token);
    }

    @Override
    public String getCsrfToken(String token) {
        return csrfStore.get(token);
    }

    @Override
    public boolean validateCsrf(String token, String csrfToken) {
        String expect = csrfStore.get(token);
        return expect != null && expect.equals(csrfToken);
    }

    @Override
    public boolean invalidate(String token) {
        csrfStore.remove(token);
        return tokenStore.remove(token) != null;
    }

    @Override
    public String issueToken(String userNum, String orgCode) {
        // 生成包含机构信息的token
        String token = UUID.randomUUID().toString().replaceAll("-", "");
        // 存储格式：userNum:orgCode
        tokenStore.put(token, userNum + ":" + orgCode);
        String csrfToken = UUID.randomUUID().toString().replaceAll("-", "");
        csrfStore.put(token, csrfToken);
        return token;
    }

    @Override
    public String getUserNumFromToken(String token) {
        String value = tokenStore.get(token);
        if (value != null) {
            // 如果存储的是userNum:orgCode格式，则只返回userNum
            if (value.contains(":")) {
                return value.split(":")[0];
            }
            return value;
        }
        return null;
    }


    /**
     * 查询用户的所有机构部门信息
     * @param user 用户实体
     * @return 用户的所有机构部门信息列表
     */
    @Override
    public List<Map<String, Object>> getUserAllOrgDeptInfo(User user) {
        log.info("开始查询用户[{}]的机构部门信息", user.getUserNum());
        try {
            List<Map<String, Object>> userAllOrgDeptList = new ArrayList<>();
            List<Map<String, Object>> orgList = sysUserOrgMapper.selectUserOrgDetailsByUserNum(user.getUserNum());
            List<Map<String, Object>> deptList = sysUserDeptMapper.selectUserDeptDetailsByUserNum(user.getUserNum());
            // 把deptList嵌套在orgList中
            if (orgList != null && !orgList.isEmpty()) {
                for (Map<String, Object> org : orgList) {
                    String orgCode = (String) org.get("orgCode");
                    // 2. 查询机构下的所有部门
                    List<Map<String, Object>> deptListInOrg = deptList.stream()
                            .filter(dept -> {
                                // 避免空指针异常，先检查dept和orgCode是否为null
                                if (dept == null || orgCode == null) {
                                    return false;
                                }
                                String deptOrgCode = (String) dept.get("orgCode");
                                return orgCode.equals(deptOrgCode);
                            })
                            .collect(Collectors.toList());
                    if (deptListInOrg != null && !deptListInOrg.isEmpty()) {
                        org.put("deptList", deptListInOrg);
                        userAllOrgDeptList.add(org);
                    }
                }
            }
            return userAllOrgDeptList;
        } catch (Exception e) {
            log.error("查询用户[{}]机构部门信息失败", user.getUserNum(), e);
            throw new RuntimeException("查询机构部门信息失败");
        }
    }

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
            Map<String, Object> orgInfo = sysOrgMapper.selectOrgByOrgCode(orgCode);
            if (orgInfo == null) {
                log.error("机构[{}]不存在", orgCode);
                throw new RuntimeException("机构不存在");
            }
            
            // 3. 验证用户是否属于该机构
            Map<String, Object> userOrgRelation = sysUserOrgMapper.selectUserOrgRelation(userNum, orgCode);
            if (userOrgRelation == null) {
                log.error("用户[{}]不属于机构[{}]", userNum, orgCode);
                throw new RuntimeException("用户不属于该机构");
            }
            
            // 4. 查询用户在该机构下的部门信息
            List<Map<String, Object>> userDeptsInOrg = sysUserDeptMapper.selectAccessibleDeptsByUserNumAndOrgCode(userNum, orgCode);
            
            // 5. 查询用户角色信息
            List<Map<String, Object>> userRoles = sysUserRoleMapper.selectUserRolesWithDataScope(userNum);
            
            // 6. 查询角色的机构数据范围
            List<Map<String, Object>> roleOrgScopes = new ArrayList<>();
            if (!userRoles.isEmpty()) {
                List<String> roleCodes = userRoles.stream()
                        .map(role -> (String) role.get("roleCode"))
                        .collect(Collectors.toList());
                roleOrgScopes = sysRoleOrgMapper.selectRoleOrgScopesByRoleCodes(roleCodes);
            }
            
            // 7. 计算用户在该机构下的数据范围
            Integer dataScopeType = calculateDataScope(userRoles, roleOrgScopes, orgCode);
            
            // 8. 查询用户所有权限
            List<Map<String, Object>> allPermissions = userMapper.selectAllPermissionsByUserNumAndOrgCode(userNum, orgCode);
            
            // 9. 提取菜单权限
            List<Map<String, Object>> menuPermissions = allPermissions.stream()
                    .filter(perm -> {
                        Integer permType = (Integer) perm.get("permType");
                        String actionType = (String) perm.get("actionType");
                        return permType != null && permType == 1 && "VIEW".equals(actionType);
                    })
                    .map(perm -> {
                        Map<String, Object> menu = new HashMap<>();
                        menu.put("menu_code", perm.get("permCode"));
                        menu.put("menu_name", perm.get("permName"));
                        menu.put("menu_type", "M");
                        menu.put("resource_key", perm.get("permKey"));
                        return menu;
                    })
                    .distinct()
                    .collect(Collectors.toList());
            
            // 10. 提取权限编码列表，用于查询数据权限规则
            List<String> permCodes = allPermissions.stream()
                    .map(perm -> (String) perm.get("permCode"))
                    .distinct()
                    .filter(permCode -> permCode != null && !permCode.isEmpty())
                    .collect(Collectors.toList());
            
            // 11. 查询数据权限规则
            List<Map<String, Object>> dataRules = Collections.emptyList();
            if (!permCodes.isEmpty()) {
                dataRules = userMapper.selectDataRulesByPermCodes(permCodes);
            }
            
            // 12. 提取角色编码列表，用于查询字段权限
            List<String> roleCodes = userRoles.stream()
                    .map(role -> (String) role.get("roleCode"))
                    .distinct()
                    .filter(roleCode -> roleCode != null && !roleCode.isEmpty())
                    .collect(Collectors.toList());
            
            // 13. 查询字段权限
            List<Map<String, Object>> fieldPermissions = Collections.emptyList();
            if (!roleCodes.isEmpty()) {
                fieldPermissions = userMapper.selectFieldPermissionsByRoleCodes(roleCodes);
            }
            
            // 14. 查询时间权限
            List<Map<String, Object>> timePermissions = Collections.emptyList();
            if (!permCodes.isEmpty()) {
                timePermissions = userMapper.selectTimePermissionsByPermCodes(permCodes);
            }
            
            // 15. 构建返回数据
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
            
            // 用户直接权限（当前为空列表）
            result.put("userPermissions", new ArrayList<>());
            
            // 角色权限
            result.put("rolePermissions", new ArrayList<>());
            
            // 页面权限
            result.put("pagePermissions", menuPermissions);
            
            // 数据权限
            List<Map<String, Object>> dataPermissions = dataRules.stream()
                    .map(rule -> {
                        Map<String, Object> dataPerm = new HashMap<>();
                        dataPerm.put("permCode", rule.get("permCode"));
                        dataPerm.put("dataName", rule.get("ruleName"));
                        dataPerm.put("entityType", rule.get("entityType"));
                        dataPerm.put("scopeType", rule.get("scopeType"));
                        dataPerm.put("ruleType", rule.get("ruleType"));
                        dataPerm.put("status", 1);
                        return dataPerm;
                    })
                    .collect(Collectors.toList());
            result.put("dataPermissions", dataPermissions);
            
            // 字段权限
            result.put("fieldPermissions", fieldPermissions);
            
            // API权限（当前为空列表）
            result.put("apiPermissions", new ArrayList<>());
            
            // 业务权限（当前为空列表）
            result.put("bizPermissions", new ArrayList<>());
            
            // 时间权限
            result.put("timePermissions", timePermissions);
            
            log.info("用户[{}]在机构[{}]的访问权限检查完成", userNum, orgCode);
            return result;
        } catch (Exception e) {
            log.error("检查用户[{}]在机构[{}]的访问权限失败", userNum, orgCode, e);
            throw new RuntimeException("检查访问权限失败");
        }
    }
    
    /**
     * 计算用户数据范围
     * @param userRoles 用户角色列表
     * @param roleOrgScopes 角色机构范围列表
     * @param orgCode 当前机构编码
     * @return 数据范围类型
     */
    private Integer calculateDataScope(List<Map<String, Object>> userRoles, 
                                      List<Map<String, Object>> roleOrgScopes, 
                                      String orgCode) {
        // 默认数据范围为本人
        Integer dataScopeType = DATA_SCOPE_SELF;
        
        // 遍历用户角色，取最宽的数据范围
        for (Map<String, Object> role : userRoles) {
            Integer roleDataScope = (Integer) role.get("dataScopeType");
            if (roleDataScope < dataScopeType) {
                dataScopeType = roleDataScope;
            }
        }
        
        // 如果是自定义数据范围，需要进一步计算
        if (dataScopeType == DATA_SCOPE_CUSTOM) {
            // 检查角色机构范围
            boolean hasAllDataScope = roleOrgScopes.stream()
                    .anyMatch(scope -> ORG_SCOPE_ALL.equals(scope.get("orgCode")));
            
            if (hasAllDataScope) {
                dataScopeType = DATA_SCOPE_ALL; // 全部数据
            } else {
                boolean hasOrgDataScope = roleOrgScopes.stream()
                        .anyMatch(scope -> orgCode.equals(scope.get("orgCode")));
                
                if (hasOrgDataScope) {
                    dataScopeType = DATA_SCOPE_ORG; // 本机构数据
                } else {
                    dataScopeType = DATA_SCOPE_SELF; // 本人数据
                }
            }
        }
        
        return dataScopeType;
    }

    @Override
    public String generateRefreshToken(String userNum, String orgCode) {
        // 生成刷新token
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}