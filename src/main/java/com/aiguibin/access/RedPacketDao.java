package com.aiguibin.access;

import com.aiguibin.entities.RedPacketEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * 描述：
 *
 * @author AIguibin Date time 2018/12/20 1:01
 */

@Repository
public interface RedPacketDao {
    /**
     * 获取红包信息.
     *
     * @param id --红包id
     * @return 红包具体信息
     */
    RedPacketEntity getRedPacket(Long id);
    /**
     * 扣减抢红包数.
     *
     * @param id -- 红包id
     * @return 更新记录条数
     */
    int decreaseRedPacket(Long id);
}
