package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.dto.LoginRequest;
import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.model.ApiResponse;
import com.aiguibin.platform.arch.service.AuthService;
import com.aiguibin.platform.arch.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
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
        User user = userService.findByUsername(req.getUsername());
        if (user == null) {
            return ApiResponse.error("账号不存在");
        }
        if (!userService.matchesPassword(req.getPassword(), user.getPassword())) {
            return ApiResponse.error("密码错误");
        }
        String token = authService.issueToken(user.getUserName());
        Map<String, Object> payload = new HashMap<>();
        payload.put("token", token);
        payload.put("csrfToken", authService.getCsrfToken(token));
        payload.put("username", user.getUserName());
        payload.put("usernumb", user.getUserNum());
        payload.put("chineseName", ""); // 兼容前端字段，不再使用
        return ApiResponse.success(payload);
    }

    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("OK");
    }
}