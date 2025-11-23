package com.aiguibin.online.table.config;

import com.aiguibin.online.table.entity.Menu;
import com.aiguibin.online.table.mapper.MenuMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 菜单数据初始化组件，基于MainLayout.vue中的模拟数据
 */
@Component
public class MenuBootstrap implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(MenuBootstrap.class);
    
    @Resource
    private MenuMapper menuMapper;
    
    @Override
    public void run(String... args) {
        logger.info("开始初始化菜单数据...");
        
        // 保留修改密码菜单，删除其他现有菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Menu::getId, 9L); // ID=9 是修改密码菜单
        menuMapper.delete(queryWrapper);
        logger.info("已删除现有菜单数据（保留修改密码菜单）");
        
        // 初始化新菜单数据
        initMenuData();
        
        logger.info("菜单数据初始化完成！");
    }
    
    /**
     * 初始化菜单数据
     */
    private void initMenuData() {
        String operator = "system";
        LocalDateTime now = LocalDateTime.now();
        
        // 创建主菜单
        Menu homeMenu = createMainMenu("首页", "home", 0, 1, "/home", operator, now);
        Menu changeMenu = createMainMenu("变更管理", "change", 1, 1, "/change", operator, now);
        Menu versionMenu = createMainMenu("版本管理", "version", 2, 1, "/version", operator, now);
        Menu queryMenu = createMainMenu("查询统计", "query", 3, 1, "/query", operator, now);
        Menu systemMenu = createMainMenu("系统设置", "system", 4, 1, "/system", operator, now);
        
        // 保存主菜单
        saveMenu(homeMenu);
        saveMenu(changeMenu);
        saveMenu(versionMenu);
        saveMenu(queryMenu);
        saveMenu(systemMenu);
        
        // 创建并保存子菜单
        // 变更管理子菜单
        createAndSaveSubMenu(changeMenu.getId(), "代码脚本变更申请", "change:apply", 0, 2, "/change/apply", operator, now);
        createAndSaveSubMenu(changeMenu.getId(), "代码脚本变更审批", "change:approve", 1, 2, "/change/approve", operator, now);
        createAndSaveSubMenu(changeMenu.getId(), "代码脚本变更记录", "change:record", 2, 2, "/change/record", operator, now);
        
        // 版本管理子菜单
        createAndSaveSubMenu(versionMenu.getId(), "版本计划", "version:plan", 0, 2, "/version/plan", operator, now);
        createAndSaveSubMenu(versionMenu.getId(), "版本发布", "version:release", 1, 2, "/version/release", operator, now);
        
        // 查询统计子菜单
        createAndSaveSubMenu(queryMenu.getId(), "变更趋势", "query:trend", 0, 2, "/query/trend", operator, now);
        createAndSaveSubMenu(queryMenu.getId(), "变更分析", "query:analysis", 1, 2, "/query/analysis", operator, now);
        
        // 系统设置子菜单
        createAndSaveSubMenu(systemMenu.getId(), "用户管理", "system:user", 0, 2, "/system/user", operator, now);
        createAndSaveSubMenu(systemMenu.getId(), "角色管理", "system:role", 1, 2, "/system/role", operator, now);
        createAndSaveSubMenu(systemMenu.getId(), "菜单管理", "system:menu", 2, 2, "/system/menu", operator, now);
        createAndSaveSubMenu(systemMenu.getId(), "字典管理", "system:dict", 3, 2, "/system/dict", operator, now);
    }
    
    /**
     * 创建主菜单
     */
    private Menu createMainMenu(String menuName, String menuCode, Integer sort, Integer menuType, String path, 
                              String operator, LocalDateTime now) {
        Menu menu = new Menu();
        menu.setMenuName(menuName);
        menu.setMenuCode(menuCode);
        menu.setSortOrder(sort);
        menu.setMenuType(menuType);
        menu.setPath(path);
        menu.setParentId(0L); // 主菜单父ID为0
        menu.setStatus(1); // 启用
        menu.setIsVisible(1); // 显示
        menu.setCreateTime(now);
        menu.setUpdateTime(now);
        return menu;
    }
    
    /**
     * 创建并保存子菜单
     */
    private void createAndSaveSubMenu(Long parentId, String menuName, String menuCode, Integer sort, Integer menuType, 
                                     String path, String operator, LocalDateTime now) {
        Menu menu = new Menu();
        menu.setParentId(parentId);
        menu.setMenuName(menuName);
        menu.setMenuCode(menuCode);
        menu.setSortOrder(sort);
        menu.setMenuType(menuType);
        menu.setPath(path);
        menu.setStatus(1); // 启用
        menu.setIsVisible(1); // 显示
        menu.setCreateTime(now);
        menu.setUpdateTime(now);
        
        saveMenu(menu);
    }
    
    /**
     * 保存菜单并处理ID
     */
    private void saveMenu(Menu menu) {
        menuMapper.insert(menu);
        logger.info("已创建菜单: {}", menu.getMenuName());
    }
}