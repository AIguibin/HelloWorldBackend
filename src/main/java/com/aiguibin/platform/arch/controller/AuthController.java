package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.dto.LoginRequest;
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
    public ApiResponse<?> login(@RequestBody @Validated LoginRequest req) {
        User user = userService.getUserByUserNum(req.getUserNum());
        if (user == null) {
            return ApiResponse.error("账号不存在");
        }
        if (!userService.matchesPassword(req.getPassword(), user.getPassword())) {
            return ApiResponse.error("密码错误");
        }
        String token = authService.issueToken(user.getUserNum());
        // TODO 根据用户信息中的org_code、dept_code去获取`sys_org``sys_dept`中的org_name、dept_name
        // TODO 根据用户信息获取角色权限信息
        List<Map<String, Object>> roleList=new ArrayList<>();
        Map<String, Object> payload = new HashMap<>();
        payload.put("token", token);
        payload.put("csrfToken", authService.getCsrfToken(token));
        payload.put("userName", user.getUserName());
        payload.put("userNum", user.getUserNum());
        payload.put("orgCode", user.getOrgCode());
        payload.put("orgName", ""); 
        payload.put("deptCode", user.getDeptCode());
        payload.put("deptName", ""); 
        payload.put("roles", roleList);
        return ApiResponse.success(payload);
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("OK");
    }
}