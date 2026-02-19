package com.aiguibin.platform.arch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.SysMenu;

import java.util.List;

public interface SysMenuService {

    SysMenu getById(Long id);

    SysMenu getByMenuCode(String menuCode);

    List<SysMenu> list();

    List<SysMenu> listByStatus(Integer status);

    Page<SysMenu> page(int page, int size, String menuName, String menuType);

    boolean save(SysMenu sysMenu);

    boolean updateById(SysMenu sysMenu);

    boolean removeById(Long id);

    List<SysMenu> getUserMenuTree(Long userId);

    List<String> getUserPermissions(Long userId);

    List<SysMenu> getAllMenuTree();

    List<SysMenu> getChildrenByParentCode(String parentMenuCode);
}
