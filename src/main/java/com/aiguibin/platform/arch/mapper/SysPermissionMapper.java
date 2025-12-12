package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 系统权限表Mapper接口
 * 提供sys_permission表的数据库访问方法
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 检查角色是否拥有指定权限
     * @param roleCodes 角色编码列表
     * @param permKey 权限标识
     * @return 是否拥有权限
     */
    boolean hasPermission(@Param("roleCodes") List<String> roleCodes, @Param("permKey") String permKey);

    /**
     * 根据角色编码列表查询权限
     * @param roleCodes 角色编码列表
     * @return 权限列表
     */
    List<SysPermission> findPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}