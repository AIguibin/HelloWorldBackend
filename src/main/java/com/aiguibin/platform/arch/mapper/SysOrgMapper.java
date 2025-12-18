package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface SysOrgMapper extends BaseMapper<SysOrg> {
    /**
     * 批量查询机构信息
     * @param orgCodes 机构编码列表
     * @return 机构信息列表
     */
    @Select("<script>" +
            "SELECT org_code as orgCode, org_name as orgName " +
            "FROM sys_org " +
            "WHERE org_code IN " +
            "<foreach collection='orgCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach>" +
            "AND status = 1 AND is_deleted = 0" +
            "</script>")
    List<Map<String, Object>> selectOrgInfosByIds(@Param("orgCodes") List<String> orgCodes);
    
    /**
     * 根据机构编码查询机构信息
     * @param orgCode 机构编码
     * @return 机构信息
     */
    @Select("SELECT org_code as orgCode, org_name as orgName, parent_org_code as parentOrgCode, level " +
            "FROM sys_org " +
            "WHERE org_code = #{orgCode} AND status = 1 AND is_deleted = 0")
    Map<String, Object> selectOrgByOrgCode(String orgCode);
    
    /**
     * 根据机构编码查询机构信息
     * @param orgCode 机构编码
     * @return 机构信息
     */
    @Select("SELECT org_code as orgCode, org_name as orgName " +
            "FROM sys_org " +
            "WHERE org_code = #{orgCode} AND status = 1 AND is_deleted = 0")
    Map<String, Object> selectOrgInfoByOrgCode(String orgCode);
}