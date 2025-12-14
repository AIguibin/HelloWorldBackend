package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.dto.LoginRO;
import com.aiguibin.platform.arch.dto.UserOrgDeptVO;
import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.model.ApiResponse;
import com.aiguibin.platform.arch.service.AuthService;
import com.aiguibin.platform.arch.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
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
        // TODO 根据用户信息获取角色权限信息
        List<Map<String, Object>> roleList=new ArrayList<>();
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
        if (!orgDeptList.isEmpty()) {
            // 查找主机构（这里假设第一个机构为主机构，实际业务中可能需要根据isPrimary字段判断）
            UserOrgDeptVO mainOrg = orgDeptList.get(0);
            payload.put("orgName", mainOrg.getOrgName());
            
            // 设置主部门名称
            if (!mainOrg.getDeptList().isEmpty()) {
                payload.put("deptName", mainOrg.getDeptList().get(0).getDeptName());
            } else {
                payload.put("deptName", "");
            }
        } else {
            payload.put("orgName", "");
            payload.put("deptName", "");
        }
        
        return ApiResponse.success(payload);
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("OK");
    }
}