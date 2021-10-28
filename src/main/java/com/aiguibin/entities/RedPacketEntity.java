package com.aiguibin.entities;

import lombok.Data;

import java.sql.Timestamp;
/**
 * 描述： 红包表对应的实体类
 *
 * @author AIguibin Date time 2018/12/20 0:57
 */

@Data
public class RedPacketEntity {
    // 红包编号
    private Long id;
    // 发红包的用户id
    private Long userId;
    // 红包金额
    private Double amount;
    // 发红包日期
    private Timestamp sendDate;
    // 红包总数
    private Integer total;
    // 单个红包的金额
    private Double unitAmount;
    // 红包剩余个数
    private Integer stock;
    // 版本（为后续扩展用）
    private Integer version;
    // 备注
    private String note;
}
