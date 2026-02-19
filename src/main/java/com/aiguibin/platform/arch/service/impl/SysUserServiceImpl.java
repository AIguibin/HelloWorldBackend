package com.aiguibin.platform.arch.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiguibin.platform.arch.entity.SysUser;
import com.aiguibin.platform.arch.mapper.SysUserMapper;
import com.aiguibin.platform.arch.service.SysUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser getById(Long id) {
        return baseMapper.selectById(id);
    }

    @Override
    public SysUser getByUserNum(String userNum) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserNum, userNum);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public SysUser getByUserName(String userName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUserName, userName);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<SysUser> list() {
        return baseMapper.selectList(null);
    }

    @Override
    public Page<SysUser> page(int page, int size, String userNum, String userName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (userNum != null && !userNum.isEmpty()) {
            wrapper.eq(SysUser::getUserNum, userNum);
        }
        if (userName != null && !userName.isEmpty()) {
            wrapper.like(SysUser::getUserName, userName);
        }
        wrapper.orderByDesc(SysUser::getCreatedTime);
        return baseMapper.selectPage(new Page<>(page, size), wrapper);
    }

    @Override
    public boolean save(SysUser sysUser) {
        return baseMapper.insert(sysUser) > 0;
    }

    @Override
    public boolean updateById(SysUser sysUser) {
        return baseMapper.updateById(sysUser) > 0;
    }

    @Override
    public boolean removeById(Long id) {
        return baseMapper.deleteById(id) > 0;
    }

    @Override
    public boolean updateLoginInfo(String userNum, String ip) {
        SysUser user = getByUserNum(userNum);
        if (user == null) {
            return false;
        }
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(ip);
        user.setLoginCount(user.getLoginCount() == null ? 1 : user.getLoginCount() + 1);
        return updateById(user);
    }

    @Override
    public boolean lockUser(String userNum, String reason) {
        SysUser user = getByUserNum(userNum);
        if (user == null) {
            return false;
        }
        user.setIsLocked(1);
        user.setLockTime(new Date());
        user.setLockReason(reason);
        return updateById(user);
    }

    @Override
    public boolean unlockUser(String userNum) {
        SysUser user = getByUserNum(userNum);
        if (user == null) {
            return false;
        }
        user.setIsLocked(0);
        user.setLockTime(null);
        user.setLockReason(null);
        return updateById(user);
    }

    @Override
    public boolean matchesPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
