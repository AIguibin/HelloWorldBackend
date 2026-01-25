package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.dto.ApproveDTO;

/**
 * 变更审批服务
 */
public interface DictChangeApproveService {

    /**
     * 审批变更
     */
    void approveChange(String changeId, ApproveDTO dto);

    /**
     * 撤回审批
     */
    void revokeApprove(String changeId);
}
