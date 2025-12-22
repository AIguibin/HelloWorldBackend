package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysFieldPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 字段权限表Mapper接口
 * 提供sys_field_permission表的数据库访问方法
 */
@Mapper
public interface SysFieldPermissionMapper extends BaseMapper<SysFieldPermission> {
    
    /**
     * 根据权限编码列表查询字段权限详情
     * @param permCodes 权限编码列表
     * @return 字段权限列表
     */
    @Select("<script>" +
            "SELECT perm_code as permCode, field_code as fieldCode, entity_type as entityType, field_name as fieldName, field_alias as fieldAlias, field_type as fieldType, status " +
            "FROM sys_field_permission " +
            "WHERE 1=1 " +
            "<if test='permCodes != null and permCodes.size() > 0'>" +
            "AND perm_code IN " +
            "<foreach collection='permCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach> " +
            "</if>" +
            "AND status = 1 AND is_deleted = 0 " +
            "</script>")
    List<Map<String, Object>> selectByPermCodes(@Param("permCodes") List<String> permCodes);
}