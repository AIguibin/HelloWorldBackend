package com.aiguibin.access;

import com.aiguibin.entities.UserInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 描述： 用户信息
 *
 * @author AIguibin Date time 2018/12/23 23:33
 */
@Repository
public interface UserInfoDao {
    /**
     * 新增用户
     * @param userInfoEntity 用户实体
     * @return 记录数int
     */
    int insertUser(UserInfoEntity userInfoEntity);
    /**
     * 修改用户信息
     * @param userInfoEntity 用户实体
     * @return 修改结果
     */
    int updateUser(UserInfoEntity userInfoEntity);
    /**
     * 查询所有用户
     * @return
     */
    List<UserInfoEntity> selectUser();
}
