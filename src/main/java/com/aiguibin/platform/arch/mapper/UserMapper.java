package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
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
    Map<String, Object> selectMainOrgDeptByUserNum(@Param("userNum") String userNum);
    
    /**
     * 根据用户编号查询用户基本信息
     * @param userNum 用户编号
     * @return 用户基本信息
     */
    @Select("SELECT user_num as userNum, user_name as userName, nickname, gender, " +
            "email, phone, avatar, status, is_locked, is_special " +
            "FROM sys_user " +
            "WHERE user_num = #{userNum} AND is_deleted = 0")
    Map<String, Object> selectUserByUserNum(@Param("userNum") String userNum);
    
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
    Map<String, Object> selectMainDeptInfoByUserNumAndOrgCode(@Param("userNum") String userNum, @Param("orgCode") String orgCode);
    

    
    /**
     * 根据用户编号和机构编码查询用户所有权限（通过统一权限表）
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户权限列表
     */
    @Select("SELECT DISTINCT sp.id, " +
            "sp.perm_code as permCode, " +
            "sp.perm_name as permName, " +
            "sp.perm_key as permKey, " +
            "sp.perm_type as permType, " +
            "sp.action_type as actionType, " +
            "sp.effect_type as effectType, " +
            "sp.condition_expression as conditionExpression, " +
            "sp.sort_order as sortOrder, " +
            "sp.status, " +
            "sp.description, " +
            "sp.created_by as createdBy, " +
            "sp.created_time as createdTime, " +
            "sp.updated_by as updatedBy, " +
            "sp.updated_time as updatedTime, " +
            "sp.is_deleted as isDeleted " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.user_num = ur.user_num AND ur.status = 1 AND ur.is_deleted = 0 " +
            "JOIN sys_role r ON ur.role_code = r.role_code AND r.status = 1 AND r.is_deleted = 0 " +
            "JOIN sys_role_permission rp ON r.role_code = rp.role_code AND rp.status = 1 AND rp.is_deleted = 0 " +
            "JOIN sys_permission sp ON rp.perm_code = sp.perm_code AND sp.status = 1 AND sp.is_deleted = 0 " +
            "WHERE u.user_num = #{userNum} AND u.org_code = #{orgCode} AND u.status = 1 AND u.is_deleted = 0")
    List<Map<String, Object>> selectAllPermissionsByUserNumAndOrgCode(@Param("userNum") String userNum, @Param("orgCode") String orgCode);
    
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
            "JOIN sys_role_permission rp ON r.role_code = rp.role_code AND rp.status = 1 AND rp.is_deleted = 0 " +
            "JOIN sys_permission sp ON rp.perm_code = sp.perm_code AND sp.status = 1 AND sp.is_deleted = 0 " +
            "JOIN sys_menu m ON sp.menu_code = m.menu_code AND m.status = 1 AND m.is_deleted = 0 " +
            "WHERE u.user_num = #{userNum} AND u.org_code = #{orgCode} AND u.status = 1 AND u.is_deleted = 0 AND sp.perm_type IN (1, 2)")
    List<String> selectUserMenusByUserNumAndOrgCode(@Param("userNum") String userNum, @Param("orgCode") String orgCode);
    
    /**
     * 根据用户编号和机构编码查询用户按钮权限
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户按钮权限列表
     */
    @Select("SELECT DISTINCT sp.perm_key " +
            "FROM sys_user u " +
            "JOIN sys_user_role ur ON u.user_num = ur.user_num AND ur.status = 1 AND ur.is_deleted = 0 " +
            "JOIN sys_role r ON ur.role_code = r.role_code AND r.status = 1 AND r.is_deleted = 0 " +
            "JOIN sys_role_permission rp ON r.role_code = rp.role_code AND rp.status = 1 AND rp.is_deleted = 0 " +
            "JOIN sys_permission sp ON rp.perm_code = sp.perm_code AND sp.status = 1 AND sp.is_deleted = 0 " +
            "WHERE u.user_num = #{userNum} AND u.org_code = #{orgCode} AND u.status = 1 AND u.is_deleted = 0 AND sp.perm_type = 2")
    List<String> selectUserPermissionsByUserNumAndOrgCode(@Param("userNum") String userNum, @Param("orgCode") String orgCode);
    
    /**
     * 根据权限编码列表查询数据权限规则
     * @param permCodes 权限编码列表
     * @return 数据权限规则列表
     */
    @Select("<script>" +
            "SELECT DISTINCT sdp.id, " +
            "sdp.perm_code as permCode, " +
            "sdp.data_name as dataName, " +
            "sdp.entity_type as entityType, " +
            "sdp.scope_type as scopeType, " +
            "sdp.include_children as includeChildren, " +
            "sdp.rule_type as ruleType, " +
            "sdp.custom_sql as customSql, " +
            "sdp.rule_expression as ruleExpression, " +
            "sdp.rule_priority as rulePriority, " +
            "sdp.condition_fields as conditionFields, " +
            "sdp.is_global as isGlobal, " +
            "sdp.status, " +
            "sdp.description, " +
            "sdp.created_by as createdBy, " +
            "sdp.created_time as createdTime, " +
            "sdp.updated_by as updatedBy, " +
            "sdp.updated_time as updatedTime, " +
            "sdp.is_deleted as isDeleted " +
            "FROM sys_data_permission sdp " +
            "WHERE sdp.perm_code IN " +
            "<foreach collection='permCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach>" +
            " AND sdp.status = 1 AND sdp.is_deleted = 0" +
            "</script>")
    List<Map<String, Object>> selectDataRulesByPermCodes(@Param("permCodes") List<String> permCodes);
    
    /**
     * 根据角色编码列表查询字段权限
     * @param roleCodes 角色编码列表
     * @return 字段权限列表
     */
    @Select("<script>" +
            "SELECT DISTINCT sfp.id, " +
            "sfp.uuid, " +
            "sfp.perm_code as permCode, " +
            "sfp.field_code as fieldCode, " +
            "sfp.entity_type as entityType, " +
            "sfp.field_name as fieldName, " +
            "sfp.field_alias as fieldAlias, " +
            "sfp.field_type as fieldType, " +
            "sfp.condition_expression as conditionExpression, " +
            "sfp.default_value as defaultValue, " +
            "sfp.validation_rules as validationRules, " +
            "sfp.ui_config as uiConfig, " +
            "sfp.sort_order as sortOrder, " +
            "sfp.status, " +
            "sfp.description, " +
            "sfp.created_by as createdBy, " +
            "sfp.created_time as createdTime, " +
            "sfp.updated_by as updatedBy, " +
            "sfp.updated_time as updatedTime, " +
            "sfp.is_deleted as isDeleted " +
            "FROM sys_field_permission sfp " +
            "JOIN sys_role_permission rp ON sfp.perm_code = rp.perm_code AND rp.status = 1 AND rp.is_deleted = 0 " +
            "WHERE rp.role_code IN " +
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach>" +
            " AND sfp.status = 1 AND sfp.is_deleted = 0" +
            "</script>")
    List<Map<String, Object>> selectFieldPermissionsByRoleCodes(@Param("roleCodes") List<String> roleCodes);
    
    /**
     * 根据权限编码列表查询时间权限
     * @param permCodes 权限编码列表
     * @return 时间权限列表
     */
    @Select("<script>" +
            "SELECT DISTINCT stp.id, " +
            "stp.uuid, " +
            "stp.perm_code as permCode, " +
            "stp.time_name as timeName, " +
            "stp.time_type as timeType, " +
            "stp.allowed_days as allowedDays, " +
            "stp.start_date as startDate, " +
            "stp.end_date as endDate, " +
            "stp.start_time as startTime, " +
            "stp.end_time as endTime, " +
            "stp.specific_dates as specificDates, " +
            "stp.exclude_dates as excludeDates, " +
            "stp.timezone, " +
            "stp.is_recurring as isRecurring, " +
            "stp.holiday_excluded as holidayExcluded, " +
            "stp.action_type as actionType, " +
            "stp.status, " +
            "stp.description, " +
            "stp.created_by as createdBy, " +
            "stp.created_time as createdTime, " +
            "stp.updated_by as updatedBy, " +
            "stp.updated_time as updatedTime, " +
            "stp.is_deleted as isDeleted " +
            "FROM sys_time_permission stp " +
            "WHERE stp.perm_code IN " +
            "<foreach collection='permCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach>" +
            " AND stp.status = 1 AND stp.is_deleted = 0" +
            "</script>")
    List<Map<String, Object>> selectTimePermissionsByPermCodes(@Param("permCodes") List<String> permCodes);
    
    /**
     * 查询用户可用机构列表
     * @param userNum 用户编号
     * @return 用户可用机构列表
     */
    @Select("SELECT org_code as orgCode, org_name as orgName " +
            "FROM sys_org " +
            "WHERE org_code IN (SELECT org_code FROM sys_user_org WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0) " +
            "AND status = 1 AND is_deleted = 0")
    List<Map<String, Object>> selectAvailableOrgsByUserNum(@Param("userNum") String userNum);
    
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
    List<Map<String, Object>> selectAvailableDeptsByUserNumAndOrgCode(@Param("userNum") String userNum, @Param("orgCode") String orgCode);
    
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
    List<Map<String, Object>> selectUserRolesByUserNumAndOrgCode(@Param("userNum") String userNum, @Param("orgCode") String orgCode);
}