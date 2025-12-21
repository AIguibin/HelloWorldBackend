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
            // 1. 查询用户基本信息（根据文档设计）
            log.info("开始查询用户[{}]的基本信息", userNum);
            Map<String, Object> userInfo = userMapper.selectUserByUserNum(userNum);
            if (userInfo == null) {
                log.error("用户[{}]不存在", userNum);
                throw new RuntimeException("用户不存在");
            }
            log.info("用户[{}]的基本信息查询成功，信息为: {}", userNum, userInfo);
            // 2. 查询机构信息
            log.info("开始查询机构[{}]的基本信息", orgCode);
            Map<String, Object> orgInfo = sysOrgMapper.selectOrgByOrgCode(orgCode);
            if (orgInfo == null) {
                log.error("机构[{}]不存在", orgCode);
                throw new RuntimeException("机构不存在");
            }
            log.info("机构[{}]的基本信息查询成功，信息为: {}", orgCode, orgInfo);
            
            // 3. 验证用户是否属于该机构（检查sys_user_org关联）
            log.info("开始查询用户[{}]与机构[{}]的关联关系", userNum, orgCode);
            Map<String, Object> userOrgRelation = sysUserOrgMapper.selectUserOrgRelation(userNum, orgCode);
            if (userOrgRelation == null) {
                log.error("用户[{}]不属于机构[{}]", userNum, orgCode);
                throw new RuntimeException("用户不属于该机构");
            }
            log.info("用户[{}]与机构[{}]的关联关系查询成功，信息为: {}", userNum, orgCode, userOrgRelation);
            
            // 4. 查询用户在该机构下的部门信息
            log.info("开始查询用户[{}]在机构[{}]下的部门信息", userNum, orgCode);
            List<Map<String, Object>> userDeptsInOrg = sysUserDeptMapper.selectAccessibleDeptsByUserNumAndOrgCode(userNum, orgCode);
            log.info("用户[{}]在机构[{}]下的部门信息查询成功，信息为: {}", userNum, orgCode, userDeptsInOrg);
            
            // 5. 查询用户角色信息（包含角色类型和数据范围）
            log.info("开始查询用户[{}]的角色信息，包含角色类型和数据范围", userNum);
            List<Map<String, Object>> userRoles = sysUserRoleMapper.selectUserRolesWithDataScope(userNum);
            log.info("用户[{}]的角色信息查询成功，信息为: {}", userNum, userRoles);
            
            // 6. 查询角色的机构数据范围
            List<Map<String, Object>> roleOrgScopes = new ArrayList<>();
            if (!userRoles.isEmpty()) {
                List<String> roleCodes = userRoles.stream()
                        .map(role -> (String) role.get("roleCode"))
                        .collect(Collectors.toList());
                log.info("开始查询角色[{}]的机构数据范围", roleCodes);
                roleOrgScopes = sysRoleOrgMapper.selectRoleOrgScopesByRoleCodes(roleCodes);
                log.info("角色[{}]的机构数据范围查询成功，信息为: {}", roleCodes, roleOrgScopes);
            }
            
            // 7. 计算用户在该机构下的数据范围
            log.info("开始计算用户[{}]在机构[{}]下的数据范围", userNum, orgCode);
            Integer dataScopeType = calculateDataScope(userRoles, roleOrgScopes, orgCode);
            log.info("用户[{}]在机构[{}]下的数据范围计算成功，数据范围类型为: {}", userNum, orgCode, dataScopeType);
            
            // 8. 查询可访问机构列表
            log.info("开始查询用户[{}]的可访问机构列表", userNum);
            List<Map<String, Object>> accessibleOrgs = sysUserOrgMapper.selectUserOrgDetailsByUserNum(userNum);
            log.info("用户[{}]的可访问机构列表查询成功，信息为: {}", userNum, accessibleOrgs);
            
            // 9. 查询可访问部门列表
            log.info("开始查询用户[{}]在机构[{}]下的可访问部门列表", userNum, orgCode);
            List<Map<String, Object>> accessibleDepts = sysUserDeptMapper.selectAccessibleDeptsByUserNumAndOrgCode(userNum, orgCode);
            log.info("用户[{}]在机构[{}]下的可访问部门列表查询成功，信息为: {}", userNum, orgCode, accessibleDepts);
            
            // 10. 查询用户所有权限（通过统一权限表）
            log.info("开始查询用户[{}]在机构[{}]下的所有权限", userNum, orgCode);
            List<Map<String, Object>> allPermissions = userMapper.selectAllPermissionsByUserNumAndOrgCode(userNum, orgCode);
            log.info("用户[{}]在机构[{}]下的所有权限查询成功，信息为: {}", userNum, orgCode, allPermissions);
            
            // 11. 从所有权限中提取菜单权限
            log.info("开始提取用户[{}]的菜单权限", userNum);
            // 注意：新的权限模型中，菜单权限通过sys_perm_resource表关联，这里暂时返回空列表
            List<String> menus = Collections.emptyList();
            log.info("用户[{}]的菜单权限提取成功，信息为: {}", userNum, menus);
            
            // 12. 从所有权限中提取按钮权限
            log.info("开始提取用户[{}]的按钮权限", userNum);
            List<String> perms = allPermissions.stream()
                    .filter(perm -> {
                        Integer permType = (Integer) perm.get("permType");
                        String actionType = (String) perm.get("actionType");
                        // 新的权限模型中，permType=1是访问控制，actionType包括VIEW、CREATE、UPDATE、DELETE、EXECUTE
                        return permType != null && permType == 1;
                    })
                    .map(perm -> (String) perm.get("permKey"))
                    .distinct()
                    .filter(permKey -> permKey != null && !permKey.isEmpty())
                    .collect(Collectors.toList());
            log.info("用户[{}]的按钮权限提取成功，信息为: {}", userNum, perms);
            
            // 13. 提取权限编码列表，用于查询数据权限规则
            log.info("开始提取用户[{}]的权限编码列表", userNum);
            List<String> permCodes = allPermissions.stream()
                    .map(perm -> (String) perm.get("permCode"))
                    .distinct()
                    .filter(permCode -> permCode != null && !permCode.isEmpty())
                    .collect(Collectors.toList());
            log.info("用户[{}]的权限编码列表提取成功，信息为: {}", userNum, permCodes);
            
            // 14. 查询数据权限规则
            log.info("开始查询用户[{}]的数据权限规则", userNum);
            List<Map<String, Object>> dataRules = Collections.emptyList();
            if (!permCodes.isEmpty()) {
                dataRules = userMapper.selectDataRulesByPermCodes(permCodes);
            }
            log.info("用户[{}]的数据权限规则查询成功，信息为: {}", userNum, dataRules);
            
            // 15. 提取角色编码列表，用于查询字段权限
            log.info("开始提取用户[{}]的角色编码列表", userNum);
            List<String> roleCodes = userRoles.stream()
                    .map(role -> (String) role.get("roleCode"))
                    .distinct()
                    .filter(roleCode -> roleCode != null && !roleCode.isEmpty())
                    .collect(Collectors.toList());
            log.info("用户[{}]的角色编码列表提取成功，信息为: {}", userNum, roleCodes);
            
            // 16. 查询字段权限
            log.info("开始查询用户[{}]的字段权限", userNum);
            List<Map<String, Object>> fieldPermissions = Collections.emptyList();
            if (!roleCodes.isEmpty()) {
                fieldPermissions = userMapper.selectFieldPermissionsByRoleCodes(roleCodes);
            }
            log.info("用户[{}]的字段权限查询成功，信息为: {}", userNum, fieldPermissions);
            
            // 17. 查询时间权限
            log.info("开始查询用户[{}]的时间权限", userNum);
            List<Map<String, Object>> timePermissions = Collections.emptyList();
            if (!permCodes.isEmpty()) {
                timePermissions = userMapper.selectTimePermissionsByPermCodes(permCodes);
            }
            log.info("用户[{}]的时间权限查询成功，信息为: {}", userNum, timePermissions);
            
            // 18. 构建返回数据
            Map<String, Object> result = new HashMap<>();
            
            // 用户基本信息
            result.put("user", userInfo);
            
            // 当前选择的机构
            Map<String, Object> currentOrg = new HashMap<>();
            currentOrg.put("orgCode", orgInfo.get("orgCode"));
            currentOrg.put("orgName", orgInfo.get("orgName"));
            currentOrg.put("orgFullPath", orgInfo.get("orgName")); 
            currentOrg.put("parentOrgCode", orgInfo.get("parentOrgCode"));
            currentOrg.put("position", userOrgRelation.get("position"));
            currentOrg.put("isPrimary", userOrgRelation.get("isPrimary"));
            currentOrg.put("selectedTime", new Date());
            result.put("currentOrg", currentOrg);
            
            // 当前部门信息（取主部门）
            Map<String, Object> currentDept = null;
            if (!userDeptsInOrg.isEmpty()) {
                currentDept = userDeptsInOrg.stream()
                        .filter(dept -> 1 == (Integer) dept.get("isPrimary"))
                        .findFirst()
                        .orElse(userDeptsInOrg.get(0));
            }
            result.put("currentDept", currentDept);
            
            // 可用机构列表
            result.put("availableOrgs", accessibleOrgs);
            
            // 可用部门列表
            result.put("availableDepts", accessibleDepts);
            
            // 构建统一权限结构
            Map<String, Object> unifiedPermissions = new HashMap<>();
            
            // 菜单权限
            unifiedPermissions.put("menuPermissions", menus);
            
            // 按钮权限
            unifiedPermissions.put("buttonPermissions", perms);
            
            // 权限集合（便于前端快速判断）
            Map<String, Boolean> permissionSet = new HashMap<>();
            for (String perm : perms) {
                permissionSet.put(perm, true);
            }
            unifiedPermissions.put("permissionSet", permissionSet);
            
            // 数据权限
            Map<String, Object> dataPermission = new HashMap<>();
            dataPermission.put("rules", dataRules);
            dataPermission.put("scopeType", dataScopeType);
            dataPermission.put("scopeTypeLabel", getDataScopeLabel(dataScopeType));
            dataPermission.put("accessibleOrgs", accessibleOrgs.stream()
                    .map(org -> (String) org.get("orgCode"))
                    .collect(Collectors.toList()));
            dataPermission.put("accessibleDeptCodes", accessibleDepts.stream()
                    .map(dept -> (String) dept.get("deptCode"))
                    .collect(Collectors.toList()));
            unifiedPermissions.put("dataPermission", dataPermission);
            
            // 字段权限
            unifiedPermissions.put("fieldPermissions", fieldPermissions);
            
            // 时间权限
            unifiedPermissions.put("timePermissions", timePermissions);
            
            // 角色信息
            unifiedPermissions.put("roles", userRoles);
            
            // 所有原始权限数据（用于高级权限控制）
            unifiedPermissions.put("allPermissions", allPermissions);
            
            // 将统一权限结构放入结果
            result.put("permissions", unifiedPermissions);
            
            // 构建简化的数据范围（保持兼容性）
            Map<String, Object> dataScope = new HashMap<>();
            dataScope.put("scopeType", dataScopeType);
            dataScope.put("scopeTypeLabel", getDataScopeLabel(dataScopeType));
            
            // 简化的授权信息（保持兼容性）
            Map<String, Object> authorization = new HashMap<>();
            authorization.put("currentRoles", userRoles);
            authorization.put("dataScope", dataScope);
            result.put("authorization", authorization);
            
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
    
    /**
     * 获取数据范围标签
     * @param dataScopeType 数据范围类型
     * @return 数据范围标签
     */
    private String getDataScopeLabel(Integer dataScopeType) {
        switch (dataScopeType) {
            case DATA_SCOPE_ALL: return "全部数据";
            case DATA_SCOPE_ORG: return "本机构数据";
            case DATA_SCOPE_DEPT: return "本部门数据";
            case DATA_SCOPE_SELF: return "本人数据";
            case DATA_SCOPE_CUSTOM: return "自定义数据";
            default: return "未知数据范围";
        }
    }

    @Override
    public String generateRefreshToken(String userNum, String orgCode) {
        // 生成刷新token
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}