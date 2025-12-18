package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 查询用户主机构部门信息
     * @param userNum 用户编号
     * @return 用户主机构部门信息
     */
    @Select("SELECT u.org_code as orgCode, o.org_name as orgName, " +
            "u.dept_code as deptCode, d.dept_name as deptName " +
            "FROM sys_user u " +
            "LEFT JOIN sys_org o ON u.org_code = o.org_code AND o.status = 1 AND o.is_deleted = 0 " +
            "LEFT JOIN sys_dept d ON u.dept_code = d.dept_code AND d.status = 1 AND d.is_deleted = 0 " +
            "WHERE u.user_num = #{userNum} AND u.status = 1 AND u.is_deleted = 0")
    Map<String, Object> selectMainOrgDeptByUserNum(String userNum);
    
    /**
     * 根据用户编号查询用户基本信息
     * @param userNum 用户编号
     * @return 用户基本信息
     */
    @Select("SELECT user_num as userNum, user_name as userName, nickname, gender, " +
            "email, phone, avatar, status, is_locked, is_special " +
            "FROM sys_user " +
            "WHERE user_num = #{userNum} AND is_deleted = 0")
    Map<String, Object> selectUserByUserNum(String userNum);
    
    /**
     * 根据用户编号和机构编码查询用户主部门信息
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户主部门信息
     */
    @Select("SELECT u.dept_code as deptCode, d.dept_name as deptName " +
            "FROM sys_user u " +
            "LEFT JOIN sys_dept d ON u.dept_code = d.dept_code AND d.status = 1 AND d.is_deleted = 0 " +
            "WHERE u.user_num = #{userNum} AND u.org_code = #{orgCode} AND u.status = 1 AND u.is_deleted = 0")
    Map<String, Object> selectMainDeptInfoByUserNumAndOrgCode(String userNum, String orgCode);
    
    /**
     * 根据用户编号和机构编码查询用户菜单权限
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户菜单权限列表
     */
    @Select("SELECT DISTINCT m.menu_code " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.user_num = ur.user_num AND ur.status = 1 AND ur.is_deleted = 0 " +
            "JOIN sys_role r ON ur.role_code = r.role_code AND r.status = 1 AND r.is_deleted = 0 " +
            "JOIN sys_role_menu rm ON r.role_code = rm.role_code AND rm.status = 1 AND rm.is_deleted = 0 " +
            "JOIN sys_menu m ON rm.menu_code = m.menu_code AND m.status = 1 AND m.is_deleted = 0 " +
            "WHERE u.user_num = #{userNum} AND u.org_code = #{orgCode} AND u.status = 1 AND u.is_deleted = 0")
    java.util.List<String> selectUserMenusByUserNumAndOrgCode(String userNum, String orgCode);
    
    /**
     * 根据用户编号和机构编码查询用户按钮权限
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户按钮权限列表
     */
    @Select("SELECT DISTINCT p.perm_code " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.user_num = ur.user_num AND ur.status = 1 AND ur.is_deleted = 0 " +
            "JOIN sys_role r ON ur.role_code = r.role_code AND r.status = 1 AND r.is_deleted = 0 " +
            "JOIN sys_role_perm rp ON r.role_code = rp.role_code AND rp.status = 1 AND rp.is_deleted = 0 " +
            "JOIN sys_perm p ON rp.perm_code = p.perm_code AND p.status = 1 AND p.is_deleted = 0 " +
            "WHERE u.user_num = #{userNum} AND u.org_code = #{orgCode} AND u.status = 1 AND u.is_deleted = 0")
    java.util.List<String> selectUserPermissionsByUserNumAndOrgCode(String userNum, String orgCode);
    
    /**
     * 查询用户可用机构列表
     * @param userNum 用户编号
     * @return 用户可用机构列表
     */
    @Select("SELECT org_code as orgCode, org_name as orgName " +
            "FROM sys_org " +
            "WHERE org_code IN (SELECT org_code FROM sys_user_org WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0) " +
            "AND status = 1 AND is_deleted = 0")
    java.util.List<Map<String, Object>> selectAvailableOrgsByUserNum(String userNum);
    
    /**
     * 根据用户编号和机构编码查询用户可用部门列表
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户可用部门列表
     */
    @Select("SELECT dept_code as deptCode, dept_name as deptName " +
            "FROM sys_dept " +
            "WHERE dept_code IN (SELECT dept_code FROM sys_user_dept WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0) " +
            "AND org_code = #{orgCode} AND status = 1 AND is_deleted = 0")
    java.util.List<Map<String, Object>> selectAvailableDeptsByUserNumAndOrgCode(String userNum, String orgCode);
    
    /**
     * 根据用户编号和机构编码查询用户角色信息
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户角色信息列表
     */
    @Select("SELECT r.role_code as roleCode, r.role_name as roleName, r.role_type as roleType " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.user_num = ur.user_num AND ur.status = 1 AND ur.is_deleted = 0 " +
            "JOIN sys_role r ON ur.role_code = r.role_code AND r.status = 1 AND r.is_deleted = 0 " +
            "WHERE u.user_num = #{userNum} AND u.org_code = #{orgCode} AND u.status = 1 AND u.is_deleted = 0")
    java.util.List<Map<String, Object>> selectUserRolesByUserNumAndOrgCode(String userNum, String orgCode);
}