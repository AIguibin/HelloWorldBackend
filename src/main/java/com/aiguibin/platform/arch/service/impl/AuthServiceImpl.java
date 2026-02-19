package com.aiguibin.platform.arch.service.impl;

import cn.hutool.core.util.IdUtil;
import com.aiguibin.platform.arch.entity.SysUser;
import com.aiguibin.platform.arch.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();
    private final Map<String, String> csrfTokenStore = new ConcurrentHashMap<>();
    private static final long TOKEN_EXPIRE_TIME = 7200 * 1000;

    @Override
    public String issueToken(String userNum) {
        String token = IdUtil.fastSimpleUUID();
        tokenStore.put(token, userNum);
        return token;
    }

    @Override
    public String issueToken(String userNum, String orgCode) {
        String token = IdUtil.fastSimpleUUID();
        tokenStore.put(token, userNum + "|" + orgCode);
        return token;
    }

    @Override
    public String getUserNumByToken(String token) {
        String value = tokenStore.get(token);
        if (value != null && value.contains("|")) {
            return value.split("\\|")[0];
        }
        return value;
    }

    @Override
    public String getUserNumFromToken(String token) {
        String value = tokenStore.get(token);
        if (value != null && value.contains("|")) {
            return value.split("\\|")[0];
        }
        return value;
    }

    @Override
    public String getCsrfToken(String token) {
        String csrfToken = IdUtil.fastSimpleUUID();
        csrfTokenStore.put(token, csrfToken);
        return csrfToken;
    }

    @Override
    public boolean validateCsrf(String token, String csrfToken) {
        String storedCsrf = csrfTokenStore.get(token);
        return storedCsrf != null && storedCsrf.equals(csrfToken);
    }

    @Override
    public boolean invalidate(String token) {
        tokenStore.remove(token);
        csrfTokenStore.remove(token);
        return true;
    }

    @Override
    public List<Map<String, Object>> getUserAllOrgDeptInfo(SysUser sysUser) {
        List<Map<String, Object>> result = new ArrayList<>();
        Map<String, Object> orgDept = new HashMap<>();
        orgDept.put("orgCode", sysUser.getOrgCode());
        orgDept.put("deptCode", sysUser.getDeptCode());
        orgDept.put("orgName", "默认机构");
        orgDept.put("deptName", "默认部门");
        result.add(orgDept);
        return result;
    }

    @Override
    public Map<String, Object> checkOrgAccess(String userNum, String orgCode) {
        Map<String, Object> result = new HashMap<>();
        result.put("userNum", userNum);
        result.put("orgCode", orgCode);
        result.put("permissions", Arrays.asList("*:*:*"));
        return result;
    }

    @Override
    public String generateRefreshToken(String userNum, String orgCode) {
        return IdUtil.fastSimpleUUID();
    }
}
