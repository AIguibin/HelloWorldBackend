package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户角色关联表Mapper接口
 * 提供sys_user_role表的数据库访问方法
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
    
    /**
     * 根据角色编码查询用户编号列表
     * @param roleCode 角色编码
     * @return 用户编号列表
     */
    List<String> selectUserNumsByRoleCode(@Param("roleCode") String roleCode);
    
    /**
     * 根据部门编码查询用户编号列表
     * @param deptCode 部门编码
     * @return 用户编号列表
     */
    List<String> selectUserNumsByDeptCode(@Param("deptCode") String deptCode);
    
    /**
     * 根据职位编码查询用户编号列表
     * @param positionCode 职位编码
     * @return 用户编号列表
     */
    List<String> selectUserNumsByPositionCode(@Param("positionCode") String positionCode);
    
    /**
     * 根据用户编号查询用户所有角色信息
     * @param userNum 用户编号
     * @return 用户角色信息列表
     */
    @Select("SELECT r.role_code as roleCode, r.role_name as roleName, ur.is_primary as isPrimary " +
            "FROM sys_user_role ur " +
            "JOIN sys_role r ON ur.role_code = r.role_code " +
            "WHERE ur.user_num = #{userNum} AND ur.status = 1 AND ur.is_deleted = 0 AND r.status = 1 AND r.is_deleted = 0")
    List<Map<String, Object>> selectUserRolesByUserNum(@Param("userNum") String userNum);
    
    /**
     * 根据用户编号查询用户角色信息，包括角色所属机构
     * @param userNum 用户编号
     * @return 用户角色及机构信息列表
     */
    @Select("SELECT r.role_code as roleCode, r.role_name as roleName, ur.is_primary as isPrimary, ro.org_code as orgCode " +
            "FROM sys_user_role ur " +
            "JOIN sys_role r ON ur.role_code = r.role_code " +
            "LEFT JOIN sys_role_org ro ON ur.role_code = ro.role_code " +
            "WHERE ur.user_num = #{userNum} AND ur.status = 1 AND ur.is_deleted = 0 AND r.status = 1 AND r.is_deleted = 0 AND (ro.is_deleted IS NULL OR ro.is_deleted = 0)")
    List<Map<String, Object>> selectUserRolesWithOrgByUserNum(@Param("userNum") String userNum);
    
    /**
     * 根据用户编号查询用户角色信息，包含角色类型和数据范围
     * @param userNum 用户编号
     * @return 用户角色信息列表
     */
    @Select("SELECT r.role_code as roleCode, r.role_name as roleName, r.role_type as roleType, r.data_scope_type as dataScopeType, ur.is_primary as isPrimary " +
            "FROM sys_user_role ur " +
            "JOIN sys_role r ON ur.role_code = r.role_code " +
            "WHERE ur.user_num = #{userNum} AND ur.status = 1 AND ur.is_deleted = 0 AND r.status = 1 AND r.is_deleted = 0")
    List<Map<String, Object>> selectUserRolesWithDataScope(@Param("userNum") String userNum);
}