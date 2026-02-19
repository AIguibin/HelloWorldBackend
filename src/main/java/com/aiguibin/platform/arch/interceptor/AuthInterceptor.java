package com.aiguibin.platform.arch.interceptor;

import com.aiguibin.platform.arch.entity.SysUser;
import com.aiguibin.platform.arch.mapper.SysUserMapper;
import com.aiguibin.platform.arch.service.AuthService;
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
    private SysUserMapper sysUserMapper;

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
        String userNum = authService.getUserNumFromToken(token);
        if (userNum == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "登录已失效");
        }

        // 从请求头获取用户编号和用户姓名（可能包含非 ASCII，解码后再校验）

        // 查询服务端用户信息，确保存在且有效
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserNum, userNum);
        SysUser sysUser = sysUserMapper.selectOne(wrapper);
        if (sysUser == null || sysUser.getUserNum() == null || sysUser.getUserNum().isEmpty() || sysUser.getUserName() == null || sysUser.getUserName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "缺少用户编号或用户姓名");
        }
        String reqUsernumb = decodeHeader(request.getHeader("X-User-Numb"));
        String reqUsername = decodeHeader(request.getHeader("X-User-Name"));
        // 若请求头提供了用户编号/姓名，则进行一致性校验
        if (reqUsernumb != null && !reqUsernumb.isEmpty() && !sysUser.getUserNum().equals(reqUsernumb)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "用户编号不一致");
        }
        if (reqUsername != null && !reqUsername.isEmpty() && !sysUser.getUserName().equals(reqUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "用户姓名不一致");
        }

        // CSRF 校验：对状态变更方法强制要求 X-CSRF-Token
        String method = request.getMethod();
        if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method) || "DELETE".equalsIgnoreCase(method)) {
            String csrf = request.getHeader("X-CSRF-Token");
            if (!authService.validateCsrf(token, csrf)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CSRF校验失败");
            }
            // 编辑权限限制：暂时保留原有逻辑，后续将替换为基于新权限字段的访问控制
            String uri = request.getRequestURI();
            if (uri.startsWith("/api/change-records")) {
                String un = sysUser.getUserNum();
                if (!("admin".equalsIgnoreCase(un) || "BG001".equalsIgnoreCase(un) || "BG002".equalsIgnoreCase(un))) {
                      // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "编辑权限限制：仅admin、BG001、BG002可编辑");
                }
            }
        }

        // 将操作人设置为 "userNum|userName" 格式，便于审计追溯
        request.setAttribute("operator", sysUser.getUserNum() + "|" + sysUser.getUserName());
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