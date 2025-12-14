package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    List<Map<String, Object>> selectOrgInfosByIds(List<String> orgCodes);
}