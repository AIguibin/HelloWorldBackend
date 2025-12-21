package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.mapper.SysOrgMapper;
import com.aiguibin.platform.arch.mapper.SysUserDeptMapper;
import com.aiguibin.platform.arch.mapper.SysUserOrgMapper;
import com.aiguibin.platform.arch.service.UserOrgDeptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户机构部门服务实现类
 * 负责查询用户的机构和部门信息等
 */
@Service
@Slf4j
public class UserOrgDeptServiceImpl implements UserOrgDeptService {

    @Autowired
    private SysUserOrgMapper sysUserOrgMapper;
    
    @Autowired
    private SysUserDeptMapper sysUserDeptMapper;
    
    @Autowired
    private SysOrgMapper sysOrgMapper;

    @Override
    public List<Map<String, Object>> getUserAllOrgDeptInfo(User user) {
        log.info("开始查询用户[{}]的机构部门信息", user.getUserNum());
        try {
            List<Map<String, Object>> userAllOrgDeptList = new ArrayList<>();
            List<Map<String, Object>> orgList = selectUserOrgDetailsByUserNum(user.getUserNum());
            List<Map<String, Object>> deptList = selectUserDeptDetailsByUserNum(user.getUserNum());
            // 把deptList嵌套在orgList中
            if (orgList != null && !orgList.isEmpty()) {
                for (Map<String, Object> org : orgList) {
                    String orgCode = (String) org.get("orgCode");
                    // 查询机构下的所有部门
                    List<Map<String, Object>> deptListInOrg = deptList.stream()
                            .filter(dept -> {
                                // 避免空指针异常，先检查dept和orgCode是否为null
                                if (dept == null || orgCode == null) {
                                    return false;
                                }
                                String deptOrgCode = (String) dept.get("orgCode");
                                return orgCode.equals(deptOrgCode);
                            })
                            .collect(Collectors.toList());
                    if (deptListInOrg != null && !deptListInOrg.isEmpty()) {
                        org.put("deptList", deptListInOrg);
                        userAllOrgDeptList.add(org);
                    }
                }
            }
            return userAllOrgDeptList;
        } catch (Exception e) {
            log.error("查询用户[{}]机构部门信息失败", user.getUserNum(), e);
            throw new RuntimeException("查询机构部门信息失败");
        }
    }

    @Override
    public List<Map<String, Object>> selectUserOrgDetailsByUserNum(String userNum) {
        return sysUserOrgMapper.selectUserOrgDetailsByUserNum(userNum);
    }

    @Override
    public List<Map<String, Object>> selectUserDeptDetailsByUserNum(String userNum) {
        return sysUserDeptMapper.selectUserDeptDetailsByUserNum(userNum);
    }

    @Override
    public Map<String, Object> selectUserOrgRelation(String userNum, String orgCode) {
        return sysUserOrgMapper.selectUserOrgRelation(userNum, orgCode);
    }

    @Override
    public List<Map<String, Object>> selectAccessibleDeptsByUserNumAndOrgCode(String userNum, String orgCode) {
        return sysUserDeptMapper.selectAccessibleDeptsByUserNumAndOrgCode(userNum, orgCode);
    }

    @Override
    public Map<String, Object> selectOrgByOrgCode(String orgCode) {
        return sysOrgMapper.selectOrgByOrgCode(orgCode);
    }
}