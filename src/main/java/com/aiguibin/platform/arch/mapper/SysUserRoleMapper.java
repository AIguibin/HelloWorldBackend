package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
}