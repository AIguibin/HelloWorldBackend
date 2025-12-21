package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.entity.User;
import java.util.List;
import java.util.Map;

/**
 * 用户机构部门服务接口
 * 负责查询用户的机构和部门信息等
 */
public interface UserOrgDeptService {
    /**
     * 查询用户的所有机构部门信息
     * @param user 用户实体
     * @return 用户的所有机构部门信息列表
     */
    List<Map<String, Object>> getUserAllOrgDeptInfo(User user);
    
    /**
     * 查询用户的机构详情
     * @param userNum 用户编号
     * @return 机构详情列表
     */
    List<Map<String, Object>> selectUserOrgDetailsByUserNum(String userNum);
    
    /**
     * 查询用户的部门详情
     * @param userNum 用户编号
     * @return 部门详情列表
     */
    List<Map<String, Object>> selectUserDeptDetailsByUserNum(String userNum);
    
    /**
     * 验证用户机构关系
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 用户机构关系Map
     */
    Map<String, Object> selectUserOrgRelation(String userNum, String orgCode);
    
    /**
     * 查询用户在指定机构下的可访问部门
     * @param userNum 用户编号
     * @param orgCode 机构编码
     * @return 可访问部门列表
     */
    List<Map<String, Object>> selectAccessibleDeptsByUserNumAndOrgCode(String userNum, String orgCode);
    
    /**
     * 查询机构信息
     * @param orgCode 机构编码
     * @return 机构信息Map
     */
    Map<String, Object> selectOrgByOrgCode(String orgCode);
}