package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysUserOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysUserOrgMapper extends BaseMapper<SysUserOrg> {
    /**
     * 查询用户扩展机构编码列表
     * @param userNum 用户编号
     * @return 机构编码列表
     */
    @Select("SELECT DISTINCT org_code " +
            "FROM sys_user_org " +
            "WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0")
    List<String> selectExtOrgCodesByUserNum(String userNum);
}