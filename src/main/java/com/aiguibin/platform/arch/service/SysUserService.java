package com.aiguibin.platform.arch.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiguibin.platform.arch.entity.SysUser;

import java.util.List;

public interface SysUserService {

    SysUser getById(Long id);

    SysUser getByUserNum(String userNum);

    SysUser getByUserName(String userName);

    List<SysUser> list();

    Page<SysUser> page(int page, int size, String userNum, String userName);

    boolean save(SysUser sysUser);

    boolean updateById(SysUser sysUser);

    boolean removeById(Long id);

    boolean updateLoginInfo(String userNum, String ip);

    boolean lockUser(String userNum, String reason);

    boolean unlockUser(String userNum);

    boolean matchesPassword(String rawPassword, String encodedPassword);
}
