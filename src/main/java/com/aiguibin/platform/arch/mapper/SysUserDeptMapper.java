package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysUserDept;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysUserDeptMapper extends BaseMapper<SysUserDept> {

        /**
         * 通过用户编号查询sys_user_dept表记录列表
         * 
         * @param userNum 用户编号
         * @return SysUserDept实体对象集合
         */
        @Select("SELECT * FROM sys_user_dept WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0")
        List<SysUserDept> selectUserDeptEntitiesByUserNum(@Param("userNum") String userNum);

        /**
         * 通过用户编号查询用户所属部门详细信息（关联sys_dept表）
         * 
         * @param userNum 用户编号
         * @return 包含部门详细信息和用户职位的结果集
         */
        @Select("SELECT sd.dept_code as deptCode, sd.dept_name as deptName, sd.org_code as orgCode, sd.sort_order as deptSortOrder, " +
                        "sd.description as deptDescription, sd.manager_num as deptManagerNum, sud.position as position " +
                        "FROM sys_user_dept sud " +
                        "LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code " +
                        "WHERE sud.user_num = #{userNum} AND sud.status = 1 AND sud.is_deleted = 0 AND sd.status = 1 AND sd.is_deleted = 0")
        List<Map<String, Object>> selectUserDeptDetailsByUserNum(@Param("userNum") String userNum);

        /**
         * 查询用户在指定机构下可访问的部门编码列表
         * 
         * @param userNum 用户编号
         * @param orgCode 机构编码
         * @return 用户可访问部门编码列表
         */
        @Select("SELECT sud.dept_code " +
                        "FROM sys_user_dept sud " +
                        "LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code AND sd.status = 1 AND sd.is_deleted = 0 " +
                        "WHERE sud.user_num = #{userNum} AND sd.org_code = #{orgCode} AND sud.status = 1 AND sud.is_deleted = 0")
        List<String> selectAccessibleDeptCodesByUserNumAndOrgCode(@Param("userNum") String userNum,
                        @Param("orgCode") String orgCode);

        /**
         * 查询用户在指定机构下可访问的部门列表
         * 
         * @param userNum 用户编号
         * @param orgCode 机构编码
         * @return 用户可访问部门列表
         */
        @Select("SELECT sd.dept_code as deptCode, sd.dept_name as deptName, sd.org_code as orgCode, sd.sort_order as deptSortOrder, " +
                        "sd.description as deptDescription, sd.manager_num as deptManagerNum, sud.position as position " +
                        "FROM sys_user_dept sud " +
                        "LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code AND sd.status = 1 AND sd.is_deleted = 0 "+
                        "WHERE sud.user_num = #{userNum} AND sd.org_code = #{orgCode} AND sud.status = 1 AND sud.is_deleted = 0")
        List<Map<String, Object>> selectAccessibleDeptsByUserNumAndOrgCode(@Param("userNum") String userNum,
                        @Param("orgCode") String orgCode);
}