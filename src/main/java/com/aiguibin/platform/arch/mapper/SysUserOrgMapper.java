package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysUserOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysUserOrgMapper extends BaseMapper<SysUserOrg> {
    /**
     * 通过用户编号查询sys_user_org表记录列表
     * @param userNum 用户编号
     * @return SysUserOrg实体对象集合
     */
    @Select("SELECT * FROM sys_user_org WHERE user_num = #{userNum} AND status = 1 AND is_deleted = 0")
    List<SysUserOrg> selectUserOrgEntitiesByUserNum(@Param("userNum") String userNum);
    
    /**
     * 通过用户编号查询用户所属机构详细信息（关联sys_org表）
     * 主要返回sys_org的信息以及sys_user_org的position(用户在机构中的职位)
     * @param userNum 用户编号
     * @return 包含机构详细信息和用户职位的结果集
     */
    @Select("SELECT uo.org_code as orgCode, uo.user_num as userNum, uo.position as position, " +
            "uo.is_primary as isPrimary, uo.effective_start as effectiveStart, uo.effective_end as effectiveEnd, " +
            "o.org_name as orgName, o.parent_org_code as parentOrgCode, o.level as orgLevel, " +
            "o.sort_order as orgSortOrder, o.description as orgDescription " +
            "FROM sys_user_org uo " +
            "LEFT JOIN sys_org o ON uo.org_code = o.org_code " +
            "WHERE uo.user_num = #{userNum} AND uo.status = 1 AND uo.is_deleted = 0 AND o.is_deleted = 0")
    List<Map<String, Object>> selectUserOrgDetailsByUserNum(@Param("userNum") String userNum);
    
    /**
     * 查询用户与机构的关联关系
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户机构关联关系
     */
    @Select("SELECT org_code as orgCode, is_primary as isPrimary, position as position " +
            "FROM sys_user_org " +
            "WHERE user_num = #{userNum} AND org_code = #{orgCode} AND status = 1 AND is_deleted = 0")
    Map<String, Object> selectUserOrgRelation(@Param("userNum") String userNum, @Param("orgCode") String orgCode);
}