package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.entity.User;
import com.aiguibin.platform.arch.mapper.UserMapper;
import com.aiguibin.platform.arch.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类.
 * 实现用户相关的各种服务，包括用户查询、验证等.
 */
@Service
public final class UserServiceImpl implements UserService {

    /**
     * 用户Mapper.
     */
    @Resource
    private UserMapper userMapper;
    
    /**
     * BCrypt密码编码器.
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User getUserByUserNum(final String userNum) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserNum, userNum)
                   .eq(User::getIsDeleted, 0);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserByUserName(final String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName)
                   .eq(User::getIsDeleted, 0);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Page<User> getUserList(final int page, final int size) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getIsDeleted, 0);
        return userMapper.selectPage(new Page<>(page, size), queryWrapper);
    }

    @Override
    public boolean isUserExists(final String userNum) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserNum, userNum)
                   .eq(User::getIsDeleted, 0);
        return userMapper.selectCount(queryWrapper) > 0;
    }

    @Override
    public List<String> getUserRoles(final String userNum) {
        // 简化实现，实际应从角色关联表查询
        return new ArrayList<>();
    }

    @Override
    public boolean verifyPassword(final String userNum, final String password) {
        User user = getUserByUserNum(userNum);
        if (user == null) {
            return false;
        }
        // 使用BCrypt验证密码
        return matchesPassword(password, user.getPassword());
    }

    @Override
    public List<String> getUserPermissions(final String userNum) {
        // 简化实现，实际应从权限关联表查询
        return new ArrayList<>();
    }

    @Override
    public boolean hasRole(final String userNum, final String roleCode) {
        // 简化实现，实际应从角色关联表查询
        return false;
    }

    @Override
    public boolean hasPermission(final String userNum, final String permCode) {
        // 简化实现，实际应从权限关联表查询
        return false;
    }

    @Override
    public User findByUsername(final String username) {
        return getUserByUserName(username);
    }

    @Override
    public boolean matchesPassword(final String password, final String encryptedPassword) {
        // 使用BCrypt验证密码
        return passwordEncoder.matches(password, encryptedPassword);
    }

    @Override
    public List<String> getUserNumsByRoleCode(final String roleCode) {
        // 简化实现，实际应从角色关联表查询
        return new ArrayList<>();
    }

    @Override
    public List<String> getUserNumsByDeptCode(final String deptCode) {
        // 简化实现，实际应从部门关联表查询
        return new ArrayList<>();
    }

    @Override
    public List<String> getUserNumsByPositionCode(final String positionCode) {
        // 简化实现，实际应从职位关联表查询
        return new ArrayList<>();
    }

    @Override
    public boolean isValidUser(final String userNum) {
        User user = getUserByUserNum(userNum);
        return user != null && user.getStatus() == 1 && user.getIsLocked() == 0;
    }

    @Override
    public String getUserNameByNum(final String userNum) {
        User user = getUserByUserNum(userNum);
        return user != null ? user.getUserName() : "";
    }
}
