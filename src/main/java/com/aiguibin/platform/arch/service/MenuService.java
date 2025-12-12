package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.entity.Menu;
import com.aiguibin.platform.arch.mapper.MenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Resource
    private MenuMapper menuMapper;

    /**
     * 根据用户ID获取菜单树
     */
    public List<Menu> getUserMenuTree(Long userId) {
        List<Menu> menus = menuMapper.selectMenusByUserId(userId);
        return buildMenuTree(menus);
    }

    /**
     * 获取所有菜单树
     */
    public List<Menu> getAllMenuTree() {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Menu::getStatus, 1);
        wrapper.orderByAsc(Menu::getSortOrder);
        wrapper.orderByAsc(Menu::getId);
        List<Menu> menus = menuMapper.selectList(wrapper);
        return buildMenuTree(menus);
    }

    /**
     * 构建菜单树
     */
    private List<Menu> buildMenuTree(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            return new ArrayList<>();
        }

        // 过滤出目录和菜单类型（排除按钮和接口）
        List<Menu> visibleMenus = menus.stream()
                .filter(m -> m.getMenuType() == 1 || m.getMenuType() == 2)
                .collect(Collectors.toList());

        // 找出所有根节点（parentMenuCode为空或为根目录编码）
        List<Menu> rootMenus = visibleMenus.stream()
                .filter(m -> m.getParentMenuCode() == null || m.getParentMenuCode().isEmpty())
                .collect(Collectors.toList());

        // 递归构建树
        for (Menu root : rootMenus) {
            buildChildren(root, visibleMenus);
        }

        return rootMenus;
    }

    /**
     * 递归构建子菜单
     */
    private void buildChildren(Menu parent, List<Menu> allMenus) {
        List<Menu> children = allMenus.stream()
                .filter(m -> parent.getMenuCode().equals(m.getParentMenuCode()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (Menu child : children) {
                buildChildren(child, allMenus);
            }
        }
    }

    /**
     * 根据用户ID获取权限标识列表
     */
    public List<String> getUserPermissions(Long userId) {
        List<Menu> menus = menuMapper.selectMenusByUserId(userId);
        return menus.stream()
                .filter(m -> m.getPermissionKey() != null && !m.getPermissionKey().isEmpty())
                .map(Menu::getPermissionKey)
                .distinct()
                .collect(Collectors.toList());
    }
}


