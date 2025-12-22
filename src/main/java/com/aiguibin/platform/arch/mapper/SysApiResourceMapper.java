package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.SysApiResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * API资源定义表Mapper接口
 * 对应sys_api_resource表，用于操作API资源定义数据
 */
@Mapper
public interface SysApiResourceMapper extends BaseMapper<SysApiResource> {

    /**
     * 查询所有API资源
     * @return API资源列表
     */
    @Select("SELECT api_code as apiCode, api_name as apiName, api_path as apiPath, http_method as httpMethod, resource_key as resourceKey, service_name as serviceName, need_auth as needAuth, status " +
            "FROM sys_api_resource " +
            "WHERE status = 1 AND is_deleted = 0 " +
            "ORDER BY service_name ASC, sort_order ASC")
    List<Map<String, Object>> selectAll();
}