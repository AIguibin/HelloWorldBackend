package com.aiguibin.core.excel;

import com.aiguibin.core.common.ExcelHelper;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 * Excel表名提取服务（支持从SQL文本中提取表名并写入指定列）
 */
public class ExcelTableNameExtractor {

    // 默认配置常量
    private static final int DEFAULT_SQL_COLUMN_INDEX = 0;
    private static final int DEFAULT_RESULT_COLUMN_INDEX = 8;


    /**
     * 构建默认表名匹配正则表达式
     * 匹配规则：不区分大小写的FROM关键字后第一个非空字符序列（忽略子查询）
     *
     * @return 预编译的正则表达式对象
     */
    public static Pattern buildDefaultTableNamePattern() {
        return Pattern.compile(
                "(?i)\\bFROM\\s+([^\\s(]+)",
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );
    }

    /**
     * 处理单个Excel文件（使用默认配置）
     *
     * @param inputFile  输入文件路径
     * @param outputFile 输出文件路径
     */
    public static void processFile(Path inputFile, Path outputFile) {
        processFile(inputFile, outputFile, DEFAULT_SQL_COLUMN_INDEX,
                DEFAULT_RESULT_COLUMN_INDEX, buildDefaultTableNamePattern());
    }

    /**
     * 处理单个Excel文件（全参数配置）
     *
     * @param inputFile         输入文件路径
     * @param outputFile        输出文件路径
     * @param sqlColumnIndex    SQL文本列索引
     * @param resultColumnIndex 结果列索引
     * @param tableNamePattern  表名匹配正则
     */
    public static void processFile(Path inputFile, Path outputFile,
                                   int sqlColumnIndex, int resultColumnIndex,
                                   Pattern tableNamePattern) {
        ExcelHelper.processWorkbookWithHandler(inputFile, outputFile, workbook -> {
            Sheet sheet = workbook.getSheetAt(0); // 处理第一个Sheet
            ExcelHelper.processSheetForTableNames(sheet, sqlColumnIndex,
                    resultColumnIndex, tableNamePattern);
        });
    }

    /**
     * 批量处理目录中的Excel文件
     *
     * @param directory         目录路径
     * @param sqlColumnIndex    SQL文本列索引
     * @param resultColumnIndex 结果列索引
     * @param tableNamePattern  表名匹配正则
     */
    public static void processDirectory(Path directory, int sqlColumnIndex,
                                        int resultColumnIndex, Pattern tableNamePattern) {
        try {
            ExcelHelper.batchProcess(directory,
                    path -> path.toString().endsWith(".xlsx"), // 过滤Excel文件
                    workbook -> {
                        Sheet sheet = workbook.getSheetAt(0);
                        ExcelHelper.processSheetForTableNames(sheet, sqlColumnIndex,
                                resultColumnIndex, tableNamePattern);
                    }
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}