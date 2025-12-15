package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.dto.LoginRO;
import com.aiguibin.platform.arch.dto.OrgDeptInfoVO;
import com.aiguibin.platform.arch.dto.UserOrgDeptVO;
import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.model.ApiResponse;
import com.aiguibin.platform.arch.service.AuthService;
import com.aiguibin.platform.arch.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class AuthController {

    @Resource
    private UserService userService;
    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody @Validated LoginRO req) {
        User user = userService.getUserByUserNum(req.getUserNum());
        if (user == null) {
            return ApiResponse.error("账号不存在");
        }
        if (!userService.matchesPassword(req.getPassword(), user.getPassword())) {
            return ApiResponse.error("密码错误");
        }
        String token = authService.issueToken(user.getUserNum());
        // 获取用户机构部门信息
        List<UserOrgDeptVO> orgDeptList = authService.getUserOrgDeptInfo(user.getUserNum());
        // 根据用户信息获取角色权限信息
        List<Map<String, Object>> roleList = userService.getUserDetailedRoles(user.getUserNum());
        Map<String, Object> payload = new HashMap<>();
        payload.put("token", token);
        payload.put("csrfToken", authService.getCsrfToken(token));
        payload.put("userName", user.getUserName());
        payload.put("userNum", user.getUserNum());
        payload.put("orgCode", user.getOrgCode());
        payload.put("deptCode", user.getDeptCode());
        payload.put("roles", roleList);
        payload.put("orgDeptList", orgDeptList);
        
        // 设置主机构和主部门名称
        String orgName = "";
        String deptName = "";
        
        // 获取用户的主机构和主部门编码
        String userOrgCode = user.getOrgCode();
        String userDeptCode = user.getDeptCode();
        
        // 遍历机构部门列表，查找匹配的主机构和主部门
        if (!orgDeptList.isEmpty() && userOrgCode != null) {
            for (UserOrgDeptVO orgDept : orgDeptList) {
                if (userOrgCode.equals(orgDept.getOrgCode())) {
                    // 找到匹配的主机构
                    orgName = orgDept.getOrgName();
                    
                    // 在该机构下查找匹配的主部门
                    if (userDeptCode != null && !orgDept.getDeptList().isEmpty()) {
                        for (OrgDeptInfoVO dept : orgDept.getDeptList()) {
                            if (userDeptCode.equals(dept.getDeptCode())) {
                                deptName = dept.getDeptName();
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }
        
        payload.put("orgName", orgName);
        payload.put("deptName", deptName);
        return ApiResponse.success(payload);
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("OK");
    }
}