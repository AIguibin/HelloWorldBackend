package com.aiguibin.online.table.excel;


import com.aiguibin.online.table.entity.CodeScriptChangeRecord;
import com.aiguibin.online.table.mapper.CodeScriptChangeRecordMapper;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ExcelExport {

    private static final Logger logger = LoggerFactory.getLogger(ExcelExport.class);
    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Scheduled(cron = "0 0 0 * * ?")
    public void main() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSSS");
        String file = String.format("%s%s%s.xlsx", System.getProperty("user.dir"), File.separator, format.format(System.currentTimeMillis()));
        try (FileOutputStream output = new FileOutputStream(file)) {
            ExcelWriter writer = EasyExcel.write(output, ExcelEntity.class).excelType(ExcelTypeEnum.XLSX).build();

            WriteSheet sheet = EasyExcel.writerSheet("首页").build();
            Map<String, Object> objectMap = new HashMap<>();
            try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
                CodeScriptChangeRecordMapper mapper = sqlSession.getMapper(CodeScriptChangeRecordMapper.class);
                Cursor<CodeScriptChangeRecord> cursor = mapper.selectCursor(objectMap);
                Integer count = doWork(cursor, 10, list -> {
                    writer.write(list, sheet);
                    return list.size();
                });
                logger.info("导出多少笔数据: {}", count);
            } catch (Exception e) {
                e.printStackTrace();
            }
            writer.finish();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T, I extends Integer> Integer doWork(Cursor<T> cursor, int pageSize, Function<List<T>, I> function) {

        List<T> list = new ArrayList<>();
        for (T entity : cursor) {
            list.add(entity);
            if (list.size() == pageSize) {
                function.apply(list);
                list = new ArrayList<>();
            }
        }

        if (list.size() > 0) {
            function.apply(list);
        }
        return cursor.getCurrentIndex() + 1;
    }
}
