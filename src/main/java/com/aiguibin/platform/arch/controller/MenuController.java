package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.Menu;
import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.mapper.UserMapper;
import com.aiguibin.platform.arch.dto.ResultVO;
import com.aiguibin.platform.arch.service.AuthService;
import com.aiguibin.platform.arch.service.MenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    @Resource
    private MenuService menuService;

    @Resource
    private AuthService authService;

    @Resource
    private UserMapper userMapper;

    /**
     * 获取当前用户的菜单树
     */
    @GetMapping("/userMenus")
    public ResultVO<List<Menu>> getUserMenus(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            // 如果无法获取用户ID，返回空菜单
            return ResultVO.success(new ArrayList<>());
        }
        List<Menu> menus = menuService.getUserMenuTree(userId);
        return ResultVO.success(menus);
    }

    /**
     * 获取当前用户的权限列表
     */
    @GetMapping("/permissions")
    public ResultVO<List<String>> getUserPermissions(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        if (userId == null) {
            return ResultVO.success(new ArrayList<>());
        }
        List<String> permissions = menuService.getUserPermissions(userId);
        return ResultVO.success(permissions);
    }

    /**
     * 获取所有菜单树（管理员功能）
     */
    @GetMapping("/all")
    public ResultVO<List<Menu>> getAllMenus() {
        List<Menu> menus = menuService.getAllMenuTree();
        return ResultVO.success(menus);
    }

    /**
     * 从请求中获取当前用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String auth = request.getHeader("Authorization");
            if (auth == null || auth.isEmpty()) {
                return null;
            }
            String token = auth.replace("Bearer ", "").trim();
            String userNum = authService.getUserNumFromToken(token);
            if (userNum == null) {
                return null;
            }
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getUserNum, userNum);
            User user = userMapper.selectOne(wrapper);
            return user != null ? user.getId() : null;
        } catch (Exception e) {
            return null;
        }
    }
}