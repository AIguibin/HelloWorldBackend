package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.dto.OrgDeptInfoVO;
import com.aiguibin.platform.arch.dto.UserOrgDeptVO;
import com.aiguibin.platform.arch.mapper.SysOrgMapper;
import com.aiguibin.platform.arch.mapper.SysUserDeptMapper;
import com.aiguibin.platform.arch.mapper.SysUserOrgMapper;
import com.aiguibin.platform.arch.mapper.SysUserRoleMapper;
import com.aiguibin.platform.arch.mapper.UserMapper;
import com.aiguibin.platform.arch.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    public List<UserOrgDeptVO> getUserOrgDeptInfo(String userNum) {
        log.info("开始查询用户[{}]的机构部门信息", userNum);
        try {
            // 1. 查询用户主机构部门信息
            Map<String, Object> mainOrgDept = userMapper.selectMainOrgDeptByUserNum(userNum);
            log.info("用户[{}]主机构部门信息: {}", userNum, mainOrgDept);
            // 2. 查询用户扩展机构编码
            List<String> extOrgCodes = sysUserOrgMapper.selectExtOrgCodesByUserNum(userNum);
            log.info("用户[{}]扩展机构编码: {}", userNum, extOrgCodes);
            
            // 3. 查询用户扩展部门信息
            List<Map<String, Object>> extDepts = sysUserDeptMapper.selectExtDeptsByUserNum(userNum);
            log.info("用户[{}]扩展部门信息: {}", userNum, extDepts);
            
            // 4. 查询用户角色信息（包括角色所属机构）
            List<Map<String, Object>> userRolesWithOrg = sysUserRoleMapper.selectUserRolesWithOrgByUserNum(userNum);
            log.info("用户[{}]角色及机构信息: {}", userNum, userRolesWithOrg);
            
            // 5. 合并去重得到所有机构编码
            Set<String> allOrgCodes = new HashSet<>();
            if (mainOrgDept != null && mainOrgDept.get("orgCode") != null) {
                allOrgCodes.add((String) mainOrgDept.get("orgCode"));
            }
            if (extOrgCodes != null && !extOrgCodes.isEmpty()) {
                allOrgCodes.addAll(extOrgCodes);
            }
            log.info("用户[{}]所有机构编码: {}", userNum, allOrgCodes);
            
            // 6. 查询所有机构信息
            List<Map<String, Object>> orgInfos = new ArrayList<>();
            if (!allOrgCodes.isEmpty()) {
                orgInfos = sysOrgMapper.selectOrgInfosByIds(new ArrayList<>(allOrgCodes));
            }
            
            // 7. 构建机构映射
            Map<String, UserOrgDeptVO> orgMap = new HashMap<>();
            for (Map<String, Object> orgInfo : orgInfos) {
                String orgCode = (String) orgInfo.get("orgCode");
                String orgName = (String) orgInfo.get("orgName");
                
                UserOrgDeptVO orgDeptVO = new UserOrgDeptVO();
                orgDeptVO.setOrgCode(orgCode);
                orgDeptVO.setOrgName(orgName);
                orgDeptVO.setDeptList(new ArrayList<>());
                orgDeptVO.setRoleList(new ArrayList<>());
                
                orgMap.put(orgCode, orgDeptVO);
            }
            
            // 8. 添加主机构部门信息
            if (mainOrgDept != null) {
                String mainOrgCode = (String) mainOrgDept.get("orgCode");
                String mainDeptCode = (String) mainOrgDept.get("deptCode");
                String mainDeptName = (String) mainOrgDept.get("deptName");
                
                if (mainOrgCode != null && mainDeptCode != null) {
                    UserOrgDeptVO orgDeptVO = orgMap.get(mainOrgCode);
                    if (orgDeptVO != null) {
                        OrgDeptInfoVO deptVO = new OrgDeptInfoVO();
                        deptVO.setDeptCode(mainDeptCode);
                        deptVO.setDeptName(mainDeptName);
                        orgDeptVO.getDeptList().add(deptVO);
                    }
                }
            }
            
            // 9. 添加扩展部门信息
            if (extDepts != null && !extDepts.isEmpty()) {
                for (Map<String, Object> extDept : extDepts) {
                    String extOrgCode = (String) extDept.get("orgCode");
                    String extDeptCode = (String) extDept.get("deptCode");
                    String extDeptName = (String) extDept.get("deptName");
                    
                    if (extOrgCode != null && extDeptCode != null) {
                        UserOrgDeptVO orgDeptVO = orgMap.get(extOrgCode);
                        if (orgDeptVO != null) {
                            OrgDeptInfoVO deptVO = new OrgDeptInfoVO();
                            deptVO.setDeptCode(extDeptCode);
                            deptVO.setDeptName(extDeptName);
                            orgDeptVO.getDeptList().add(deptVO);
                        }
                    }
                }
            }
            
            // 10. 添加角色信息到对应机构
            if (userRolesWithOrg != null && !userRolesWithOrg.isEmpty()) {
                for (Map<String, Object> roleInfo : userRolesWithOrg) {
                    String orgCode = (String) roleInfo.get("orgCode");
                    
                    // 如果角色没有关联机构（orgCode为null），则将其添加到所有机构下
                    if (orgCode == null) {
                        for (UserOrgDeptVO orgDeptVO : orgMap.values()) {
                            orgDeptVO.getRoleList().add(roleInfo);
                        }
                    } else {
                        // 否则添加到指定机构下
                        UserOrgDeptVO orgDeptVO = orgMap.get(orgCode);
                        if (orgDeptVO != null) {
                            orgDeptVO.getRoleList().add(roleInfo);
                        }
                    }
                }
            }
            
            // 11. 转换为列表并返回
            List<UserOrgDeptVO> resultList = new ArrayList<>(orgMap.values());
            log.info("用户[{}]的机构部门信息查询完成，共查询到{}个机构", userNum, resultList.size());
            return resultList;
        } catch (Exception e) {
            log.error("查询用户[{}]机构部门信息失败", userNum, e);
            throw new RuntimeException("查询机构部门信息失败");
        }
    }

    @Override
    public Map<String, Object> checkOrgAccess(String userNum, String orgCode) {
        log.info("开始检查用户[{}]在机构[{}]的访问权限", userNum, orgCode);
        try {
            // 1. 查询用户基本信息
            Map<String, Object> userInfo = userMapper.selectUserInfoByUserNum(userNum);
            
            // 2. 查询机构信息
            Map<String, Object> orgInfo = sysOrgMapper.selectOrgInfoByOrgCode(orgCode);
            
            // 3. 查询部门信息
            Map<String, Object> deptInfo = userMapper.selectMainDeptInfoByUserNumAndOrgCode(userNum, orgCode);
            
            // 4. 查询权限信息
            List<String> menus = userMapper.selectUserMenusByUserNumAndOrgCode(userNum, orgCode);
            List<String> perms = userMapper.selectUserPermissionsByUserNumAndOrgCode(userNum, orgCode);
            
            // 5. 查询可访问机构
            List<String> accessibleOrgs = sysUserOrgMapper.selectAccessibleOrgCodesByUserNum(userNum);
            List<String> accessibleDepts = sysUserDeptMapper.selectAccessibleDeptCodesByUserNumAndOrgCode(userNum, orgCode);
            
            // 6. 查询用户可用机构列表
            List<Map<String, Object>> availableOrgList = userMapper.selectAvailableOrgsByUserNum(userNum);
            List<Map<String, Object>> availableDeptList = userMapper.selectAvailableDeptsByUserNumAndOrgCode(userNum, orgCode);
            
            // 7. 查询用户角色信息
            List<Map<String, Object>> roleList = userMapper.selectUserRolesByUserNumAndOrgCode(userNum, orgCode);
            
            // 8. 构建返回数据
            Map<String, Object> result = new HashMap<>();
            
            // 用户基本信息
            result.put("user", userInfo);
            
            // 当前选择的机构
            Map<String, Object> currentOrg = new HashMap<>();
            currentOrg.put("orgCode", orgInfo.get("orgCode"));
            currentOrg.put("orgName", orgInfo.get("orgName"));
            currentOrg.put("orgFullPath", orgInfo.get("orgName"));
            currentOrg.put("parentOrgCode", orgInfo.get("parentOrgCode"));
            currentOrg.put("position", "");
            currentOrg.put("isPrimary", true);
            currentOrg.put("selectedTime", new Date());
            result.put("currentOrg", currentOrg);
            
            // 当前部门
            result.put("currentDept", deptInfo);
            
            // 可用机构列表
            List<Map<String, Object>> availableOrgs = new ArrayList<>();
            for (Map<String, Object> org : availableOrgList) {
                Map<String, Object> availableOrg = new HashMap<>();
                availableOrg.put("orgCode", org.get("orgCode"));
                availableOrg.put("orgName", org.get("orgName"));
                availableOrg.put("isPrimary", orgCode.equals(org.get("orgCode")));
                availableOrg.put("position", "");
                availableOrg.put("lastSelected", orgCode.equals(org.get("orgCode")));
                availableOrgs.add(availableOrg);
            }
            result.put("availableOrgs", availableOrgs);
            
            // 可用部门列表
            List<Map<String, Object>> availableDepts = new ArrayList<>();
            for (Map<String, Object> dept : availableDeptList) {
                Map<String, Object> availableDept = new HashMap<>();
                availableDept.put("deptCode", dept.get("deptCode"));
                availableDept.put("deptName", dept.get("deptName"));
                availableDept.put("orgCode", dept.get("orgCode"));
                availableDept.put("position", "");
                availableDept.put("isPrimary", deptInfo != null && dept.get("deptCode").equals(deptInfo.get("deptCode")));
                availableDept.put("selected", deptInfo != null && dept.get("deptCode").equals(deptInfo.get("deptCode")));
                availableDepts.add(availableDept);
            }
            result.put("availableDepts", availableDepts);
            
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
            dataScope.put("scopeType", 2); // 默认本机构
            dataScope.put("scopeTypeLabel", "本机构数据");
            dataScope.put("accessibleOrgs", accessibleOrgs);
            dataScope.put("accessibleDeptCodes", accessibleDepts);
            result.put("dataScope", dataScope);
            
            // 授权信息
            Map<String, Object> authorization = new HashMap<>();
            authorization.put("currentRoles", roleList);
            authorization.put("dataScope", dataScope);
            result.put("authorization", authorization);
            
            // 会话信息
            Map<String, Object> session = new HashMap<>();
            session.put("loginTime", new Date());
            session.put("selectedOrgCode", orgCode);
            session.put("selectedOrgTime", new Date());
            session.put("sessionId", UUID.randomUUID().toString().replaceAll("-", ""));
            result.put("session", session);
            
            log.info("用户[{}]在机构[{}]的访问权限检查完成", userNum, orgCode);
            return result;
        } catch (Exception e) {
            log.error("检查用户[{}]在机构[{}]的访问权限失败", userNum, orgCode, e);
            throw new RuntimeException("检查访问权限失败");
        }
    }

    @Override
    public String generateRefreshToken(String userNum, String orgCode) {
        // 生成刷新token
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}