package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysUserDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysUserDeptMapper extends BaseMapper<SysUserDept> {
    /**
     * 查询用户部门信息
     * @param userNum 用户编号
     * @return 部门信息列表
     */
    @Select("SELECT sud.user_num, sd.* " + 
            "FROM sys_user_dept sud LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code " +
            "WHERE sud.user_num = #{userNum} AND sud.status = 1 AND sud.is_deleted = 0 AND sd.status = 1 AND sd.is_deleted = 0")
    List<Map<String, Object>> selectDeptsByUserNum(String userNum);
    
    /**
     * 查询用户扩展部门信息
     * @param userNum 用户编号
     * @return 扩展部门信息列表
     */
    @Select("SELECT sud.org_code, sud.dept_code, sd.dept_name " +
            "FROM sys_user_dept sud " +
            "LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code AND sd.status = 1 AND sd.is_deleted = 0 " +
            "WHERE sud.user_num = #{userNum} AND sud.status = 1 AND sud.is_deleted = 0")
    List<Map<String, Object>> selectExtDeptsByUserNum(String userNum);
    
    /**
     * 查询用户在指定机构下的部门信息
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户在指定机构下的部门信息列表
     */
    @Select("SELECT sud.dept_code, sd.dept_name, sud.is_primary, sud.position " +
            "FROM sys_user_dept sud " +
            "LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code AND sd.status = 1 AND sd.is_deleted = 0 " +
            "WHERE sud.user_num = #{userNum} AND sd.org_code = #{orgCode} AND sud.status = 1 AND sud.is_deleted = 0")
    List<Map<String, Object>> selectUserDeptsByUserNumAndOrgCode(String userNum, String orgCode);
    
    /**
     * 查询用户在指定机构下可访问的部门编码列表
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户可访问部门编码列表
     */
    @Select("SELECT sud.dept_code " +
            "FROM sys_user_dept sud " +
            "LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code AND sd.status = 1 AND sd.is_deleted = 0 " +
            "WHERE sud.user_num = #{userNum} AND sd.org_code = #{orgCode} AND sud.status = 1 AND sud.is_deleted = 0")
    List<String> selectAccessibleDeptCodesByUserNumAndOrgCode(String userNum, String orgCode);
    
    /**
     * 查询用户在指定机构下可访问的部门列表
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户可访问部门列表
     */
    @Select("SELECT sud.dept_code as deptCode, sd.dept_name as deptName, sud.is_primary, sud.position " +
            "FROM sys_user_dept sud " +
            "LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code AND sd.status = 1 AND sd.is_deleted = 0 " +
            "WHERE sud.user_num = #{userNum} AND sd.org_code = #{orgCode} AND sud.status = 1 AND sud.is_deleted = 0")
    List<Map<String, Object>> selectAccessibleDeptsByUserNumAndOrgCode(String userNum, String orgCode);
}