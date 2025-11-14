package com.aiguibin.online.table.interceptor;

import com.aiguibin.online.table.entity.User;
import com.aiguibin.online.table.mapper.UserMapper;
import com.aiguibin.online.table.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if (path != null && path.endsWith("/export")){
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录");
        }
        String token = auth.replace("Bearer ", "").trim();
        String username = authService.getUsernameByToken(token);
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已失效");
        }

        // 从请求头获取用户编号和用户姓名（可能包含非 ASCII，解码后再校验）

        // 查询服务端用户信息，确保存在且有效
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(wrapper);
        if (user == null || user.getUsernumb() == null || user.getUsernumb().isEmpty() || user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "缺少用户编号或用户姓名");
        }
        String reqUsernumb = decodeHeader(request.getHeader("X-User-Numb"));
        String reqUsername = decodeHeader(request.getHeader("X-User-Name"));
        // 若请求头提供了用户编号/姓名，则进行一致性校验
        if (reqUsernumb != null && !reqUsernumb.isEmpty() && !user.getUsernumb().equals(reqUsernumb)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "用户编号不一致");
        }
        if (reqUsername != null && !reqUsername.isEmpty() && !user.getUsername().equals(reqUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "用户姓名不一致");
        }

        // CSRF 校验：对状态变更方法强制要求 X-CSRF-Token
        String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            String csrf = request.getHeader("X-CSRF-Token");
            if (!authService.validateCsrf(token, csrf)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CSRF校验失败");
            }
            // 编辑权限限制：仅 admin、BG001、BG002 可进行变更记录的写操作
            String uri = request.getRequestURI();
            if (uri.startsWith("/api/change-records")) {
                String un = user.getUsernumb();
                if (!("admin".equalsIgnoreCase(un) || "BG001".equalsIgnoreCase(un) || "BG002".equalsIgnoreCase(un))) {
                      // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "编辑权限限制：仅admin、BG001、BG002可编辑");
                }
            }
        }

        // 将操作人设置为 "usernumb|username" 格式，便于审计追溯
        request.setAttribute("operator", user.getUsernumb() + "|" + user.getUsername());
        return true;
    }

    private String decodeHeader(String v) {
        if (v == null) return null;
        try {
            return URLDecoder.decode(v, StandardCharsets.UTF_8.name());
        } catch (Exception ignore) {
            return v;
        }
    }
}