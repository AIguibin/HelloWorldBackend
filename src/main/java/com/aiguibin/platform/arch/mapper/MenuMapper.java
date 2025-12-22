package com.aiguibin.platform.arch.mapper;

import com.aiguibin.platform.arch.entity.Menu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 根据用户ID查询菜单列表
     */
    List<Menu> selectMenusByUserId(@Param("userId") Long userId);

    /**
     * 根据角色ID查询菜单列表
     */
    List<Menu> selectMenusByRoleId(@Param("roleId") Long roleId);

    /**
     * 根据菜单类型查询菜单列表
     * @param menuType 菜单类型
     * @return 菜单列表
     */
    @Select("SELECT menu_code as menuCode, menu_name as menuName, menu_type as menuType, parent_menu_code as parentMenuCode, path, component, resource_key as resourceKey, resource_type as resourceType, is_visible as isVisible, sort_order as sortOrder, status " +
            "FROM sys_menu " +
            "WHERE menu_type = #{menuType} AND status = 1 AND is_deleted = 0 " +
            "ORDER BY sort_order ASC")
    List<Map<String, Object>> selectByMenuType(@Param("menuType") String menuType);

    /**
     * 根据父菜单编码查询按钮列表
     * @param parentMenuCode 父菜单编码
     * @return 按钮列表
     */
    @Select("SELECT menu_code as menuCode, menu_name as menuName, menu_type as menuType, parent_menu_code as parentMenuCode, resource_key as resourceKey, resource_type as resourceType, is_visible as isVisible, sort_order as sortOrder, status " +
            "FROM sys_menu " +
            "WHERE parent_menu_code = #{parentMenuCode} AND menu_type = 'B' AND status = 1 AND is_deleted = 0 " +
            "ORDER BY sort_order ASC")
    List<Map<String, Object>> selectButtonsByParentCode(@Param("parentMenuCode") String parentMenuCode);
}

