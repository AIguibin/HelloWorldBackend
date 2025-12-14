package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.dto.OrgDeptInfoVO;
import com.aiguibin.platform.arch.dto.UserOrgDeptVO;
import com.aiguibin.platform.arch.mapper.SysOrgMapper;
import com.aiguibin.platform.arch.mapper.SysUserDeptMapper;
import com.aiguibin.platform.arch.mapper.SysUserOrgMapper;
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
    public List<UserOrgDeptVO> getUserOrgDeptInfo(String userNum) {
        log.info("开始查询用户[{}]的机构部门信息", userNum);
        try {
            // 1. 查询用户主机构部门信息
            Map<String, Object> mainOrgDept = userMapper.selectMainOrgDeptByUserNum(userNum);
            
            // 2. 查询用户扩展机构编码
            List<String> extOrgCodes = sysUserOrgMapper.selectExtOrgCodesByUserNum(userNum);
            
            // 3. 查询用户扩展部门信息
            List<Map<String, Object>> extDepts = sysUserDeptMapper.selectExtDeptsByUserNum(userNum);
            
            // 4. 合并去重得到所有机构编码
            Set<String> allOrgCodes = new HashSet<>();
            if (mainOrgDept != null && mainOrgDept.get("orgCode") != null) {
                allOrgCodes.add((String) mainOrgDept.get("orgCode"));
            }
            if (extOrgCodes != null && !extOrgCodes.isEmpty()) {
                allOrgCodes.addAll(extOrgCodes);
            }
            
            // 5. 查询所有机构信息
            List<Map<String, Object>> orgInfos = new ArrayList<>();
            if (!allOrgCodes.isEmpty()) {
                orgInfos = sysOrgMapper.selectOrgInfosByIds(new ArrayList<>(allOrgCodes));
            }
            
            // 6. 构建机构映射
            Map<String, UserOrgDeptVO> orgMap = new HashMap<>();
            for (Map<String, Object> orgInfo : orgInfos) {
                String orgCode = (String) orgInfo.get("orgCode");
                String orgName = (String) orgInfo.get("orgName");
                
                UserOrgDeptVO orgDeptVO = new UserOrgDeptVO();
                orgDeptVO.setOrgCode(orgCode);
                orgDeptVO.setOrgName(orgName);
                orgDeptVO.setDeptList(new ArrayList<>());
                
                orgMap.put(orgCode, orgDeptVO);
            }
            
            // 7. 添加主机构部门信息
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
            
            // 8. 添加扩展部门信息
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
            
            // 9. 转换为列表并返回
            List<UserOrgDeptVO> resultList = new ArrayList<>(orgMap.values());
            log.info("用户[{}]的机构部门信息查询完成，共查询到{}个机构", userNum, resultList.size());
            return resultList;
        } catch (Exception e) {
            log.error("查询用户[{}]机构部门信息失败", userNum, e);
            throw new RuntimeException("查询机构部门信息失败");
        }
    }
}