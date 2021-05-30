package com.aiguibin.service;

import com.aiguibin.access.RedPacketDao;
import com.aiguibin.entities.RedPacketEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述：
 *
 * @author AIguibin Date time 2018/12/20 1:10
 */
@Service
public class RedPacketServiceImpl implements RedPacketService {
    @Autowired
    private RedPacketDao redPacketDao;

    @Override
    @Transactional(isolation=Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public RedPacketEntity getRedPacket(Long id) {
        return redPacketDao.getRedPacket(id);
    }

    @Override
    @Transactional(isolation=Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int decreaseRedPacket(Long id) {
        return redPacketDao.decreaseRedPacket(id);
    }

}
