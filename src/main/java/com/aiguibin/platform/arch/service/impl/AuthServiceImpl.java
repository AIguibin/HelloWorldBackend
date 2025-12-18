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

    @Override
    public List<Map<String, Object>> getUserOrgDeptInfo(User user) {
        log.info("开始查询用户[{}]的机构部门信息", user.getUserNum());
        try {
        List<Map<String, Object>> allOrgDeptList = new ArrayList<>();
        List<Map<String, Object>> orgList = sysUserOrgMapper.selectOrgsByUserNum(user.getUserNum());
        List<Map<String, Object>> deptList = sysUserDeptMapper.selectDeptsByUserNum(user.getUserNum());
        // 把deptList嵌套在orgList中
        if (orgList != null && !orgList.isEmpty()) {
            for (Map<String, Object> org : orgList) {
                String orgCode = (String) org.get("org_code");
                // 2. 查询机构下的所有部门
                List<Map<String, Object>> deptListInOrg = deptList.stream()
                        .filter(dept -> orgCode.equals(dept.get("org_code")))
                        .collect(Collectors.toList());
                if (deptListInOrg != null && !deptListInOrg.isEmpty()) {
                    org.put("deptList", deptListInOrg);
                    allOrgDeptList.add(org);
                }
            }
        }
            return allOrgDeptList;
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
            
            // 3. 验证用户是否属于该机构（检查sys_user_org关联）
            Map<String, Object> userOrgRelation = sysUserOrgMapper.selectUserOrgRelation(userNum, orgCode);
            if (userOrgRelation == null) {
                log.error("用户[{}]不属于机构[{}]", userNum, orgCode);
                throw new RuntimeException("用户不属于该机构");
            }
            
            // 4. 查询用户在该机构下的部门信息
            List<Map<String, Object>> userDeptsInOrg = sysUserDeptMapper.selectUserDeptsByUserNumAndOrgCode(userNum, orgCode);
            
            // 5. 查询用户角色信息（包含角色类型和数据范围）
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
            
            // 8. 查询可访问机构列表
            List<Map<String, Object>> accessibleOrgs = sysUserOrgMapper.selectAccessibleOrgsByUserNum(userNum);
            
            // 9. 查询可访问部门列表
            List<Map<String, Object>> accessibleDepts = sysUserDeptMapper.selectAccessibleDeptsByUserNumAndOrgCode(userNum, orgCode);
            
            // 10. 查询用户菜单权限
            List<String> menus = userMapper.selectUserMenusByUserNumAndOrgCode(userNum, orgCode);
            
            // 11. 查询用户按钮权限
            List<String> perms = userMapper.selectUserPermissionsByUserNumAndOrgCode(userNum, orgCode);
            
            // 12. 构建返回数据
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
            
            // 权限信息
            Map<String, Object> permissions = new HashMap<>();
            permissions.put("menus", menus);
            permissions.put("perms", perms);
            
            // 构建permissionSet
            Map<String, Boolean> permissionSet = new HashMap<>();
            for (String perm : perms) {
                permissionSet.put(perm, true);
            }
            permissions.put("permissionSet", permissionSet);
            result.put("permissions", permissions);
            
            // 数据范围
            Map<String, Object> dataScope = new HashMap<>();
            dataScope.put("scopeType", dataScopeType);
            dataScope.put("scopeTypeLabel", getDataScopeLabel(dataScopeType));
            dataScope.put("accessibleOrgs", accessibleOrgs.stream()
                    .map(org -> (String) org.get("orgCode"))
                    .collect(Collectors.toList()));
            dataScope.put("accessibleDeptCodes", accessibleDepts.stream()
                    .map(dept -> (String) dept.get("deptCode"))
                    .collect(Collectors.toList()));
            result.put("dataScope", dataScope);
            
            // 授权信息
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
        Integer dataScopeType = 4;
        
        // 遍历用户角色，取最宽的数据范围
        for (Map<String, Object> role : userRoles) {
            Integer roleDataScope = (Integer) role.get("dataScopeType");
            if (roleDataScope < dataScopeType) {
                dataScopeType = roleDataScope;
            }
        }
        
        // 如果是自定义数据范围，需要进一步计算
        if (dataScopeType == 5) {
            // 检查角色机构范围
            boolean hasAllDataScope = roleOrgScopes.stream()
                    .anyMatch(scope -> "1".equals(scope.get("orgCode"))); // 假设1代表全部机构
            
            if (hasAllDataScope) {
                dataScopeType = 1; // 全部数据
            } else {
                boolean hasOrgDataScope = roleOrgScopes.stream()
                        .anyMatch(scope -> orgCode.equals(scope.get("orgCode")));
                
                if (hasOrgDataScope) {
                    dataScopeType = 2; // 本机构数据
                } else {
                    dataScopeType = 4; // 本人数据
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
            case 1: return "全部数据";
            case 2: return "本机构数据";
            case 3: return "本部门数据";
            case 4: return "本人数据";
            case 5: return "自定义数据";
            default: return "未知数据范围";
        }
    }

    @Override
    public String generateRefreshToken(String userNum, String orgCode) {
        // 生成刷新token
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}