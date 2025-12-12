package com.aiguibin.platform.arch.service;

import java.util.Map;

/**
 * 通知服务接口.
 * 用于发送各种类型的通知，包括审批通知、系统通知等.
 */
public interface NotifyService {

    /**
     * 发送审批通知.
     * @param notifyContent 通知内容，包含审批相关信息.
     * @return 是否发送成功.
     */
    boolean sendApprovalNotification(Map<String, Object> notifyContent);
}
