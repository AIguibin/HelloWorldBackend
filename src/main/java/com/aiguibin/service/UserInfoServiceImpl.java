package com.aiguibin.service;

import com.aiguibin.access.UserInfoDao;
import com.aiguibin.entities.UserInfoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * 描述： 用户信息实现
 *
 * @author AIguibin Date time 2018/12/24 0:04
 */
@Component
public class UserInfoServiceImpl implements UserInfoService {
    private UserInfoDao userInfoDao;

    @Autowired
    public UserInfoServiceImpl(UserInfoDao userInfoDao) {
        this.userInfoDao = userInfoDao;
    }

    @Override
    public int addUserInfo(UserInfoEntity userInfoEntity) {
        userInfoDao.insertUser(userInfoEntity);
        return 1;
    }

    @Override
    public int updateUser(UserInfoEntity userInfoEntity) {
        userInfoDao.updateUser(userInfoEntity);
        return 1;
    }

    @Override
    public List<UserInfoEntity> selectUser() {
        List<UserInfoEntity> entities = userInfoDao.selectUser();
        if (Optional.of(entities).isPresent() && entities.size() > 0) {
            entities.stream().map(entity -> "用户名" + entity.getUserName() + "地址" + entity.getAddress() + "生日" + entity.getBirthday()).forEach(System.out::println);
        }
        return entities;
    }
}
