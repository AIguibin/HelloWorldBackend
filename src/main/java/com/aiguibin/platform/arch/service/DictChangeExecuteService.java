package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.dto.ExecuteResultVO;

/**
 * 变更执行服务
 */
public interface DictChangeExecuteService {

    /**
     * 执行变更
     */
    ExecuteResultVO executeChange(String changeId);

    /**
     * 取消执行
     */
    void cancelExecute(String changeId);
}
