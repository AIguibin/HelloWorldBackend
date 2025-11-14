package com.aiguibin.online.table.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiguibin.online.table.entity.CodeScriptChangeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.mapping.ResultSetType;

import java.util.Map;
@Mapper
public interface CodeScriptChangeRecordMapper extends BaseMapper<CodeScriptChangeRecord> {
    @Select("select * from code_script_change_record")
    @Options(fetchSize = 5000, resultSetType = ResultSetType.FORWARD_ONLY)
    Cursor<CodeScriptChangeRecord> selectCursor(Map<String, Object> objectMap);
}