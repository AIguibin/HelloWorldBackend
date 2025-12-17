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
        // 生成临时token，用于后续机构选择
        String tempToken = authService.issueToken(user.getUserNum());
        // 获取用户机构部门信息
        List<UserOrgDeptVO> orgDeptList = authService.getUserOrgDeptInfo(user.getUserNum());
        // 根据用户信息获取角色权限信息
        List<Map<String, Object>> roleList = userService.getUserDetailedRoles(user.getUserNum());
        Map<String, Object> payload = new HashMap<>();
        payload.put("tempToken", tempToken);
        payload.put("csrfToken", authService.getCsrfToken(tempToken));
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
    
    /**
     * 机构选择后的登录验证接口
     * @param tempToken 临时token
     * @param selectedOrgCode 选择的机构编码
     * @return 完整的登录信息，包括用户、机构、权限等
     */
    @PostMapping("/check")
    public ApiResponse<?> checkLogin(@RequestBody Map<String, String> requestBody) {
        String tempToken = requestBody.get("tempToken");
        String selectedOrgCode = requestBody.get("selectedOrgCode");
        
        // 验证临时token
        if (tempToken == null || tempToken.isEmpty()) {
            return ApiResponse.error("临时token不能为空");
        }
        
        // 验证机构编码
        if (selectedOrgCode == null || selectedOrgCode.isEmpty()) {
            return ApiResponse.error("机构编码不能为空");
        }
        
        // 从临时token中获取用户信息
        String userNum = authService.getUserNumFromToken(tempToken);
        if (userNum == null) {
            return ApiResponse.error("临时token无效");
        }
        
        // 获取用户信息
        User user = userService.getUserByUserNum(userNum);
        if (user == null) {
            return ApiResponse.error("用户不存在");
        }
        
        // 获取用户在该机构下的权限信息
        Map<String, Object> result = authService.checkOrgAccess(userNum, selectedOrgCode);
        
        // 生成正式token
        String accessToken = authService.issueToken(userNum, selectedOrgCode);
        result.put("accessToken", accessToken);
        result.put("tokenType", "Bearer");
        result.put("expiresIn", 7200); // 2小时过期
        result.put("refreshToken", authService.generateRefreshToken(userNum, selectedOrgCode));
        
        return ApiResponse.success(result);
    }
}