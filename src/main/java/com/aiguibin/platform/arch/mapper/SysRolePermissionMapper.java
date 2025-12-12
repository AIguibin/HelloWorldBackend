package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysRolePermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色权限关联表Mapper接口
 * 提供sys_role_permission表的数据库访问方法
 */
@Mapper
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    /**
     * 根据角色编码列表查询权限编码列表
     * @param roleCodes 角色编码列表
     * @return 权限编码列表
     */
    List<String> findPermCodesByRoleCodes(@Param("roleCodes") List<String> roleCodes);

    /**
     * 检查角色是否拥有指定权限
     * @param roleCode 角色编码
     * @param permCode 权限编码
     * @return 是否拥有权限
     */
    boolean hasPermission(@Param("roleCode") String roleCode, @Param("permCode") String permCode);

    /**
     * 根据权限编码查询角色编码列表
     * @param permCode 权限编码
     * @return 角色编码列表
     */
    List<String> findRoleCodesByPermCode(@Param("permCode") String permCode);
}