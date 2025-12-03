package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.ChangeHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChangeHistoryMapper extends BaseMapper<ChangeHistory> {
}