package com.aiguibin.service;

import com.aiguibin.access.RedPacketDao;
import com.aiguibin.access.UserRedPacketDao;
import com.aiguibin.entities.RedPacketEntity;
import com.aiguibin.entities.UserRedPacketEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 描述：
 *
 * @author AIguibin Date time 2018/12/20 1:14
 */
@Service
public class UserRedPacketServiceImpl implements UserRedPacketService {
    // private Logger logger =
    // LoggerFactory.getLogger(UserRedPacketServiceImpl.class);

    @Autowired
    private UserRedPacketDao userRedPacketDao;

    @Autowired
    private RedPacketDao redPacketDao;

    // 失败
    private static final int FAILED = 0;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int grapRedPacket(Long redPacketId, Long userId) {
        // 获取红包信息
        RedPacketEntity redPacket = redPacketDao.getRedPacket(redPacketId);
        int leftRedPacket = redPacket.getStock();
        // 当前小红包库存大于0
        if (leftRedPacket > 0) {
            redPacketDao.decreaseRedPacket(redPacketId);
            // logger.info("剩余Stock数量:{}", leftRedPacket);
            /** 生成抢红包信息 */
            UserRedPacketEntity userRedPacket = new UserRedPacketEntity();
            userRedPacket.setRedPacketId(redPacketId);
            userRedPacket.setUserId(userId);
            userRedPacket.setAmount(redPacket.getUnitAmount());
            userRedPacket.setNote("redpacket- " + redPacketId);
            // 插入抢红包信息
            int result = userRedPacketDao.grapRedPacket(userRedPacket);
            return result;
        }
        // logger.info("没有红包啦.....剩余Stock数量:{}", leftRedPacket);
        return FAILED;
    }

}
