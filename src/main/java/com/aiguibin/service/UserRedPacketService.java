package com.aiguibin.service;

import org.springframework.stereotype.Service;

/**
 * 描述：
 *
 * @author AIguibin Date time 2018/12/20 1:11
 */
public interface UserRedPacketService {
    /**
     * 保存抢红包信息.
     * @param redPacketId 红包编号
     * @param userId 抢红包用户编号
     * @return 影响记录数.
     */
     int grapRedPacket(Long redPacketId, Long userId);
}
