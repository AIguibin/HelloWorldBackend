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
     * 查询用户扩展部门信息
     * @param userNum 用户编号
     * @return 部门信息列表
     */
    @Select("SELECT sud.org_code as orgCode, sud.dept_code as deptCode, sd.dept_name as deptName " +
            "FROM sys_user_dept sud " +
            "LEFT JOIN sys_dept sd ON sud.dept_code = sd.dept_code AND sd.status = 1 AND sd.is_deleted = 0 " +
            "WHERE sud.user_num = #{userNum} AND sud.status = 1 AND sud.is_deleted = 0")
    List<Map<String, Object>> selectExtDeptsByUserNum(String userNum);
}