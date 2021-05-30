package com.aiguibin.entities;

import lombok.Data;


/**
 * 描述： 地址信息
 *
 * @author AIguibin Date time 2018/12/23 20:27
 */
@Data
public class AddressEntity {
    private Integer id;
    /**省份*/
    private String province;
   /**城市*/
    private String city;
    /**区*/
    private String district;
    /**街道*/
    private String street;
    /**路*/
    private String road;
    /**建筑物*/
    private String building;
    /**单元*/
    private String unit;
    /**房间*/
    private String room;
}
