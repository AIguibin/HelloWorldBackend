package com.aiguibin.service;

import com.aiguibin.entities.RedPacketEntity;
import org.springframework.stereotype.Service;

/**
 * 描述：
 *
 * @author AIguibin Date time 2018/12/20 1:08
 */
public interface RedPacketService {
    /**
     * 获取红包
     * @param id ——编号
     * @return 红包信息
     */
     RedPacketEntity getRedPacket(Long id);

    /**
     * 扣减红包
     * @param id——编号
     * @return 影响条数.
     */
     int decreaseRedPacket(Long id);
}
