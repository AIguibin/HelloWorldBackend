package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 系统权限表Mapper接口
 * 提供sys_permission表的数据库访问方法
 */
@Mapper
public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 查询所有权限定义
     * @return 所有权限定义列表
     */
    @Select("SELECT perm_code as permCode, perm_name as permName, perm_key as permKey, perm_type as permType, action_type as actionType, effect_type as effectType, status, description " +
            "FROM sys_permission " +
            "WHERE status = 1 AND is_deleted = 0")
    List<Map<String, Object>> selectAll();

    /**
     * 根据角色编码列表查询权限
     * @param roleCodes 角色编码列表
     * @return 权限列表
     */
    @Select("<script>" +
            "SELECT p.perm_code as permCode, p.perm_name as permName, rpr.role_code as roleCode, rpr.auth_type as authType, rpr.effective_start as effectiveStart, rpr.effective_end as effectiveEnd " +
            "FROM sys_permission p " +
            "JOIN sys_role_permission rpr ON p.perm_code = rpr.perm_code " +
            "WHERE 1=1 " +
            "<if test='roleCodes != null and roleCodes.size() > 0'>" +
            "AND rpr.role_code IN " +
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach> " +
            "</if>" +
            "AND p.status = 1 AND p.is_deleted = 0 AND rpr.status = 1 AND rpr.is_deleted = 0" +
            "</script>")
    List<Map<String, Object>> selectPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);

    /**
     * 检查角色是否拥有指定权限
     * @param roleCodes 角色编码列表
     * @param permKey 权限标识
     * @return 是否拥有权限
     */
    @Select("<script>" +
            "SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END " +
            "FROM sys_permission p " +
            "JOIN sys_role_permission rpr ON p.perm_code = rpr.perm_code " +
            "WHERE 1=1 " +
            "<if test='roleCodes != null and roleCodes.size() > 0'>" +
            "AND rpr.role_code IN " +
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach> " +
            "</if>" +
            "AND p.perm_key = #{permKey} " +
            "AND p.status = 1 AND p.is_deleted = 0 AND rpr.status = 1 AND rpr.is_deleted = 0" +
            "</script>")
    boolean hasPermission(@Param("roleCodes") List<String> roleCodes, @Param("permKey") String permKey);

    /**
     * 根据角色编码列表查询权限
     * @param roleCodes 角色编码列表
     * @return 权限列表
     */
    @Select("<script>" +
            "SELECT DISTINCT p.* " +
            "FROM sys_permission p " +
            "JOIN sys_role_permission rpr ON p.perm_code = rpr.perm_code " +
            "WHERE 1=1 " +
            "<if test='roleCodes != null and roleCodes.size() > 0'>" +
            "AND rpr.role_code IN " +
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach> " +
            "</if>" +
            "AND p.status = 1 AND p.is_deleted = 0 AND rpr.status = 1 AND rpr.is_deleted = 0" +
            "</script>")
    List<SysPermission> findPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}