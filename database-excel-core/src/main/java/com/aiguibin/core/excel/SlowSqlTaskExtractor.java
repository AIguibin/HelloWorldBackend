package com.aiguibin.core.excel;


import com.aiguibin.core.common.FileAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 读取慢SQL文件，匹配表名称，输出禅道任务，自动创建禅道任务
 */
public class SlowSqlTaskExtractor {
    //日志声明
    private static final Log logger = LogFactory.getLog(FileAccessor.class);
    // 数据库设计文档路径
    private static final String TABLE_LIST_DIR="database-excel-core/docs/tableList";
    /**
     *
     * @return
     */
    public void tabbleListIsExist(){

        Path tableListPath = FileAccessor.getProjectRootFolderPath(TABLE_LIST_DIR);
        if (Files.notExists(tableListPath)) {
            try {
                Files.createDirectories(tableListPath);
            } catch (IOException e) {
                logger.info("目录创建失败：\n\r",e);
            }
            logger.info("目录已创建: " + TABLE_LIST_DIR);
        }
        logger.info(tableListPath.toString());
    }
}
