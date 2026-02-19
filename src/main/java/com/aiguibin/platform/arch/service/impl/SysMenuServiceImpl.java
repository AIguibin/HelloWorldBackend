package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguibin.platform.arch.entity.SysMenu;
import com.aiguibin.platform.arch.mapper.SysMenuMapper;
import com.aiguibin.platform.arch.service.SysMenuService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public SysMenu getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public SysMenu getByMenuCode(String menuCode) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getMenuCode, menuCode);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<SysMenu> list() {
        return baseMapper.selectList(null);
    }

    @Override
    public List<SysMenu> listByStatus(Integer status) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus, status);
        wrapper.orderByAsc(SysMenu::getSortOrder);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public Page<SysMenu> page(int page, int size, String menuName, String menuType) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (menuName != null && !menuName.isEmpty()) {
            wrapper.like(SysMenu::getMenuName, menuName);
        }
        if (menuType != null && !menuType.isEmpty()) {
            wrapper.eq(SysMenu::getMenuType, menuType);
        }
        wrapper.orderByAsc(SysMenu::getSortOrder);
        return baseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public boolean save(SysMenu sysMenu) {
        return baseMapper.insert(sysMenu) > 0;
    }

    @Override
    public boolean updateById(SysMenu sysMenu) {
        return baseMapper.updateById(sysMenu) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public List<SysMenu> getUserMenuTree(Long userId) {
        List<SysMenu> allMenus = listByStatus(1);
        List<SysMenu> filteredMenus = allMenus.stream()
                .filter(menu -> !"B".equals(menu.getMenuType()))
                .collect(Collectors.toList());
        return buildMenuTree(filteredMenus, null);
    }

    @Override
    public List<String> getUserPermissions(Long userId) {
        List<SysMenu> allMenus = listByStatus(1);
        return allMenus.stream()
                .map(SysMenu::getResourceKey)
                .filter(key -> key != null && !key.isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public List<SysMenu> getAllMenuTree() {
        List<SysMenu> allMenus = listByStatus(1);
        return buildMenuTree(allMenus, null);
    }

    @Override
    public List<SysMenu> getChildrenByParentCode(String parentMenuCode) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (parentMenuCode == null || parentMenuCode.isEmpty()) {
            wrapper.isNull(SysMenu::getParentMenuCode);
        } else {
            wrapper.eq(SysMenu::getParentMenuCode, parentMenuCode);
        }
        wrapper.orderByAsc(SysMenu::getSortOrder);
        return baseMapper.selectList(wrapper);
    }

    private List<SysMenu> buildMenuTree(List<SysMenu> menus, String parentCode) {
        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu menu : menus) {
            boolean isParentMatch = (parentCode == null && menu.getParentMenuCode() == null)
                    || (parentCode != null && parentCode.equals(menu.getParentMenuCode()));
            if (isParentMatch) {
                List<SysMenu> children = buildMenuTree(menus, menu.getMenuCode());
                tree.add(menu);
            }
        }
        return tree;
    }
}
