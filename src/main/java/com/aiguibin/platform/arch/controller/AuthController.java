package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.dto.LoginRO;
import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.dto.ResultVO;
import com.aiguibin.platform.arch.service.AuthService;
import com.aiguibin.platform.arch.service.UserService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

import java.util.Date;
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

    @GetMapping("/health")
    public ResultVO<String> health() {
        return ResultVO.success("OK");
    }

    @PostMapping("/auth/login")
    public ResultVO<?> login(@RequestBody @Validated LoginRO req) {
        User user = userService.getUserByUserNum(req.getUserNum());
        if (user == null) {
            return ResultVO.error("账号不存在");
        }
        if (!userService.matchesPassword(req.getPassword(), user.getPassword())) {
            return ResultVO.error("密码错误");
        }
        // 生成临时token，用于后续机构选择
        String tempToken = authService.issueToken(user.getUserNum());


                // 构建响应数据
        Map<String, Object> responseData = new HashMap<>();

        // 1. 构建用户信息
        responseData.put("userNum", user.getUserNum());
        responseData.put("userName", user.getUserName());
        responseData.put("avatar", user.getAvatar());
        responseData.put("email", user.getEmail());
        responseData.put("phone", user.getPhone());

        
        // 2. 构建token信息
        responseData.put("tempToken", tempToken);
        // 5分钟过期
        responseData.put("expiresIn", 300); 
        // 获取用户机构部门信息
        List<Map<String, Object>> userAllOrgDeptList = authService.getUserAllOrgDeptInfo(user);
        // 3.构建部门新信息
        responseData.put("userAllOrgDeptList", userAllOrgDeptList);

        return ResultVO.success("登录成功，请选择机构", responseData);
    }

    /**
     * 机构选择后的登录验证接口
     * 
     * @param tempToken       临时token
     * @param selectedOrgCode 选择的机构编码
     * @return 完整的登录信息，包括用户、机构、权限等
     */
    @PostMapping("/auth/check")
    public ResultVO<?> checkLogin(@RequestBody Map<String, String> requestBody) {
        String tempToken = requestBody.get("tempToken");
        String selectedOrgCode = requestBody.get("selectedOrgCode");

        // 验证临时token
        if (tempToken == null || tempToken.isEmpty()) {
            return ResultVO.error("临时token不能为空");
        }

        // 验证机构编码
        if (selectedOrgCode == null || selectedOrgCode.isEmpty()) {
            return ResultVO.error("机构编码不能为空");
        }

        // 从临时token中获取用户信息
        String userNum = authService.getUserNumFromToken(tempToken);
        if (userNum == null) {
            return ResultVO.error("临时token无效");
        }

        // 获取用户信息
        User user = userService.getUserByUserNum(userNum);
        if (user == null) {
            return ResultVO.error("用户不存在");
        }

        // 获取用户在该机构下的权限信息
        Map<String, Object> result = authService.checkOrgAccess(userNum, selectedOrgCode);

        // 生成正式token
        String accessToken = authService.issueToken(userNum, selectedOrgCode);
        // 获取CSRF Token
        String csrfToken = authService.getCsrfToken(accessToken);
        // 构建会话信息
        result.put("accessToken", accessToken);
        result.put("tokenType", "Bearer");
        result.put("csrfToken", csrfToken);
        result.put("expiresIn", 7200); // 2小时过期
        result.put("refreshToken", authService.generateRefreshToken(userNum, selectedOrgCode));
        result.put("loginTime", new Date());
        result.put("selectedOrgCode", selectedOrgCode);
        result.put("selectedOrgTime", new Date());
        // 按照格式生成sessionId: SESSION_当前时间_用户名_机构编码
        String sessionId = String.format("SESSION_%s_%s_%s",
                new Date().toString().replaceAll("\\s+", "_").replaceAll(":", ""),
                userNum,
                selectedOrgCode);
        result.put("sessionId", sessionId);

        return ResultVO.success(result);
    }
}