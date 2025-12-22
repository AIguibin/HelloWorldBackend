package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysPermResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 权限-资源关联表Mapper接口
 * 对应sys_perm_resource表，用于操作权限与资源的关联关系
 */
@Mapper
public interface SysPermResourceMapper extends BaseMapper<SysPermResource> {

    /**
     * 根据角色编码列表和资源类型查询资源
     * @param roleCodes 角色编码列表
     * @param resourceType 资源类型
     * @return 资源列表
     */
    @Select("<script>" +
            "SELECT spr.perm_code as permCode, spr.resource_type as resourceType, spr.resource_key as resourceKey " +
            "FROM sys_perm_resource spr " +
            "JOIN sys_role_permission rpr ON spr.perm_code = rpr.perm_code " +
            "WHERE 1=1 " +
            "<if test='roleCodes != null and roleCodes.size() > 0'>" +
            "AND rpr.role_code IN " +
            "<foreach collection='roleCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach> " +
            "</if>" +
            "AND spr.resource_type = #{resourceType} " +
            "AND spr.status = 1 AND spr.is_deleted = 0 AND rpr.status = 1 AND rpr.is_deleted = 0 " +
            "GROUP BY spr.perm_code, spr.resource_type, spr.resource_key " +
            "</script>")
    List<Map<String, Object>> selectResourcesByRoleCodes(@Param("roleCodes") List<String> roleCodes, @Param("resourceType") String resourceType);
}