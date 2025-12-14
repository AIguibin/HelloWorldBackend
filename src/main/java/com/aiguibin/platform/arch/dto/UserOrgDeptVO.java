package com.aiguibin.platform.arch.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户机构部门信息VO
 */
@Data
public class UserOrgDeptVO {
    private String orgCode;       // 机构编码
    private String orgName;       // 机构名称
    private List<OrgDeptInfoVO> deptList;  // 部门信息列表
}