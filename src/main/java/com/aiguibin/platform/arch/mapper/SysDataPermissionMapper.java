package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysDataPermission;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 数据权限子表Mapper接口
 * 对应sys_data_permission表，用于操作数据权限规则
 */
@Mapper
public interface SysDataPermissionMapper extends BaseMapper<SysDataPermission> {

    /**
     * 根据权限编码列表查询数据权限详情
     * @param permCodes 权限编码列表
     * @return 数据权限列表
     */
    @Select("<script>" +
            "SELECT perm_code as permCode, data_name as dataName, entity_type as entityType, scope_type as scopeType, include_children as includeChildren, rule_type as ruleType, custom_sql as customSql, rule_expression as ruleExpression, rule_priority as rulePriority, condition_fields as conditionFields, is_global as isGlobal, status " +
            "FROM sys_data_permission " +
            "WHERE 1=1 " +
            "<if test='permCodes != null and permCodes.size() > 0'>" +
            "AND perm_code IN " +
            "<foreach collection='permCodes' item='code' open='(' separator=',' close=')'>" +
            "#{code}" +
            "</foreach> " +
            "</if>" +
            "AND status = 1 AND is_deleted = 0 " +
            "</script>")
    List<Map<String, Object>> selectByPermCodes(@org.apache.ibatis.annotations.Param("permCodes") List<String> permCodes);
}