package com.aiguibin.online.table.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.online.table.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}