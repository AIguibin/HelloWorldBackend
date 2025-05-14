package com.aiguibin.core.excel;


import com.aiguibin.core.common.FileAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.nio.file.Path;

/**
 * 读取慢SQL文件，匹配表名称，输出禅道任务，自动创建禅道任务
 */
public class SlowSqlTaskExtractor {
    //日志声明
    private static final Log logger = LogFactory.getLog(FileAccessor.class);

    public Path tabbleListIsExist(){
        Path tableListPath = FileAccessor.getProjectRootFolderPath("database-excel-core/docs/tableList");
        return tableListPath;
    }
}
