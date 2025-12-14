package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.dto.UserOrgDeptVO;

import java.util.List;

public interface AuthService {
    String issueToken(String userNum);
    String getUserNumByToken(String token);
    String getCsrfToken(String token);
    boolean validateCsrf(String token, String csrfToken);
    boolean invalidate(String token);
    
    /**
     * 获取用户机构部门信息
     * @param userNum 用户编号
     * @return 用户机构部门信息列表
     */
    List<UserOrgDeptVO> getUserOrgDeptInfo(String userNum);
}