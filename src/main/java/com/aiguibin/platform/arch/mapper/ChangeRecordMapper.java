package com.aiguibin.platform.arch.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.platform.arch.entity.ChangeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.ResultSetType;

import java.util.Map;

@Mapper
public interface ChangeRecordMapper extends BaseMapper<ChangeRecord> {
    @Select("select * from biz_change_record")
    @Options(fetchSize = 5000, resultSetType = ResultSetType.FORWARD_ONLY)
    Cursor<ChangeRecord> selectCursor(Map<String, Object> objectMap);
}