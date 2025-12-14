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
}