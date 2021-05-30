package com.aiguibin.access;

import com.aiguibin.entities.UserRedPacketEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * 描述：
 *
 * @author AIguibin Date time 2018/12/20 1:06
 */
@Repository
public interface UserRedPacketDao {
    /**
     * 插入抢红包信息.
     *
     * @param userRedPacket ——抢红包信息
     * @return 影响记录数.
     */
    int grapRedPacket(UserRedPacketEntity userRedPacket);
}
