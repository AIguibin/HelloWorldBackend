package com.aiguibin.entities;

import lombok.Data;

import java.sql.Timestamp;

/**
 * 描述： 用户抢红包表
 *
 * @author AIguibin Date time 2018/12/20 1:00
 */

@Data
public class UserRedPacketEntity {
    // 用户红包id
    private Long id;
    // 红包id
    private Long redPacketId;
    // 抢红包的用户的id
    private Long userId;
    // 抢红包金额
    private Double amount;
    // 抢红包时间
    private Timestamp grabTime;
    // 备注
    private String note;
}
