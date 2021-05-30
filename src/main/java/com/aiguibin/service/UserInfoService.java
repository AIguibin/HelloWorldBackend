package com.aiguibin.service;

import com.aiguibin.entities.UserInfoEntity;

import java.util.List;

/**
 * 描述： 用户信息服务层
 *
 * @author AIguibin Date time 2018/12/24 0:05
 */

public interface UserInfoService {
    int addUserInfo(UserInfoEntity userInfoEntity);
    int updateUser(UserInfoEntity userInfoEntity);
    /**
     * 查询所有用户
     * @return
     */
    List<UserInfoEntity> selectUser();
}
