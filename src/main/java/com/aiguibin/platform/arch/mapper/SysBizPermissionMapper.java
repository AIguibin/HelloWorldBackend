package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysBizPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 业务权限子表Mapper接口
 * 对应sys_biz_permission表，用于操作业务权限规则
 */
@Mapper
public interface SysBizPermissionMapper extends BaseMapper<SysBizPermission> {

    /**
     * 根据权限编码列表查询业务权限详情
     * @param permCodes 权限编码列表
     * @return 业务权限列表
     */
    @Select("<script>" +
            "SELECT perm_code as permCode, business_name as businessName, business_type as businessType, rule_engine as ruleEngine, status " +
            "FROM sys_biz_permission " +
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