package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysRoleOrg;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 角色机构关联表Mapper接口
 * 提供sys_role_org表的数据库访问方法
 */
@Mapper
public interface SysRoleOrgMapper extends BaseMapper<SysRoleOrg> {
    
    /**
     * 根据角色编码列表查询角色机构范围
     * @param roleCodes 角色编码列表
     * @return 角色机构范围列表
     */
    @Select("<script>" +
            "SELECT role_code as roleCode, org_code as orgCode, org_range_type as orgRangeType, perm_type as permType " +
            "FROM sys_role_org " +
            "WHERE role_code IN " +
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach>" +
            "AND status = 1 AND is_deleted = 0" +
            "</script>")
    List<Map<String, Object>> selectRoleOrgScopesByRoleCodes(@Param("roleCodes") List<String> roleCodes);
}