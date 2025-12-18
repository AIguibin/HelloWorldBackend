package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysUserOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;


import java.util.List;
import java.util.Map;

@Mapper
public interface SysUserOrgMapper extends BaseMapper<SysUserOrg> {
    /**
     * 查询用户扩展机构编码列表
     * @param userNum 用户编号
     * @return 机构编码列表
     */
    @Select("SELECT org_code, org_name, is_primary " +
            "FROM sys_user_org " +
            "WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0")
    List<Map<String, Object>> selectOrgsByUserNum(String userNum);
    
    /**
     * 查询用户扩展机构编码列表
     * @param userNum 用户编号
     * @return 机构编码列表
     */
    @Select("SELECT org_code " +
            "FROM sys_user_org " +
            "WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0")
    List<String> selectExtOrgCodesByUserNum(String userNum);
    
    /**
     * 查询用户与机构的关联关系
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户机构关联关系
     */
    @Select("SELECT org_code, org_name, is_primary, position " +
            "FROM sys_user_org " +
            "WHERE user_num = #{userNum} AND org_code = #{orgCode} AND status = 1 AND is_deleted = 0")
    Map<String, Object> selectUserOrgRelation(String userNum, String orgCode);
    
    /**
     * 查询用户可访问机构列表
     * @param userNum 用户编号
     * @return 用户可访问机构列表
     */
    @Select("SELECT org_code as orgCode, org_name as orgName, is_primary, position " +
            "FROM sys_user_org " +
            "WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0")
    List<Map<String, Object>> selectAccessibleOrgsByUserNum(String userNum);
    
    /**
     * 查询用户可访问机构编码列表
     * @param userNum 用户编号
     * @return 用户可访问机构编码列表
     */
    @Select("SELECT org_code " +
            "FROM sys_user_org " +
            "WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0")
    List<String> selectAccessibleOrgCodesByUserNum(String userNum);
}