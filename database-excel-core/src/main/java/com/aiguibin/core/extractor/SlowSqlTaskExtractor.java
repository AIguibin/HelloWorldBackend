package com.aiguibin.core.extractor;


import com.aiguibin.core.common.ExcelHelper;
import com.aiguibin.core.common.FileAccessor;
import com.aiguibin.core.converter.SqlTableNameConverter;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.aiguibin.core.common.ExcelHelper.isExcelFile;

/**
 * 读取慢SQL文件，匹配表名称，输出禅道任务，自动创建禅道任务
 */
public class SlowSqlTaskExtractor {
    //日志声明
    private static final Log logger = LogFactory.getLog(SlowSqlTaskExtractor.class);
    // 指定目录名称
    private static final Set<String> SHEET_NAMES = new HashSet<>(Arrays.asList("目录"));
    // 数据库设计文档路径
    private static final String TABLE_LIST_DIR = "database-excel-core/docs/tableList";

    // 初步筛选慢SQL根路径
    private static final String SLOW_SQL_ROOT_PATH = "database-excel-core/docs/slowSQL";
    // 慢SQL文件处理
    private static final String SLOW_SQL_STEP_THREE_PATH = "database-excel-core/docs/stageList/stepThreeSlowSQL";
    // 慢SQL文件处理添加所属环境库表文件名
    private static final String SLOW_SQL_STEP_FOUR_PATH = "database-excel-core/docs/stageList/stepFourSlowSQL";

    // 慢SQL文件处理添加所属环境库表文件名
    private static final String SLOW_SQL_STEP_FIVE_PATH = "database-excel-core/docs/stageList/stepFiveSlowSQL";

    // 慢SQL文件处理添加所属环境库表文件名
    private static final String SLOW_SQL_STEP_SIX_PATH = "database-excel-core/docs/stageList/stepSixSlowSQL";
    // 禅道任务文档输出路径
    private static final String TASK_LIST_FILE_EXCEL = "database-excel-core/docs/taskerList/slowSqlTaskFileList.xlsx";

    /**
     *
     */
    public void createTaskBySlowSQLAnalyzer() {

        // 数据库设计表是否存在
        Path tableListPath = FileAccessor.getProjectRootFolderPath(TABLE_LIST_DIR);
        if (Files.notExists(tableListPath)) {
            try {
                Files.createDirectories(tableListPath);
            } catch (IOException e) {
                logger.debug("目录创建失败：\n\r", e);
            }
            logger.debug("目录已创建: " + TABLE_LIST_DIR);
        }
        logger.debug("当前正在处理目录" + tableListPath.toString());

        // 检查文件夹下所有的Excel文件中是否存在指定的SHEET页
        int errorSheetCount = ExcelHelper.checkSheetsInDirectory(FileAccessor.getProjectRootFolderPath(TABLE_LIST_DIR), SHEET_NAMES, true);
        logger.debug("文件指定目录不存在的数量：" + errorSheetCount);

        // 把所有的数据库表设计文档中的目录合并成一个Excel文件，取制定列，主要获取表名所属能力中心
        Path sourcePath = FileAccessor.getProjectRootFolderPath(TABLE_LIST_DIR);
        Path middleTextPath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_THREE_PATH + "/table_name_middle.txt");
        Path targetExcelPath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_THREE_PATH + "/table_name_target.xlsx");
        List<String> sheetNames = Arrays.asList("目录");
        String[] headers = {"序号", "微服务中心", "表名", "表中文名", "表注释"};
        int[] columnsToExport = {0, 1, 2, 3, 4};
        String delimiter = "@";

        // 多个Excel文件转成单个Excel文件，中间使用text（支持指定列导出）
        ExcelHelper.multipleExcelToSigleToExcelByText(sourcePath, middleTextPath, targetExcelPath, sheetNames, headers, delimiter, columnsToExport);

        // 添加负责人table_name_target.xlsx
        ExcelHelper.addResponsibleColumns(targetExcelPath, 1, 5, 6);

        // 遍历文件夹找出所有的Excel，并把所有文件名写入源文件I列，所属环境库表
        Path slowSqlRootPath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_ROOT_PATH);
        FileAccessor.traverseDirectory(new File(String.valueOf(slowSqlRootPath)), this::processExcelsqlList);
        // 复制文件夹
        try {
            FileAccessor.copyDirectory(SLOW_SQL_STEP_FOUR_PATH, SLOW_SQL_STEP_FIVE_PATH);
        } catch (IOException e) {
            logger.debug("复制文件夹失败", e);
        }


        // 增加一个处理类,处理数据
        Path slowSqlStepFivePath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_FIVE_PATH);
        FileAccessor.traverseDirectory(new File(String.valueOf(slowSqlStepFivePath)), this::processStepFiveFile);


        Path sourceDirPath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_FIVE_PATH);
        Path outputFilePath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_SIX_PATH + "/slow_sql_pre_task.xlsx");
        String[] sourceSheetNames = {"sqlList"};
        int[] skipEmptyCellOfColumn = {0};
        // 合并目录中多个Excel文件中的多个sheet页到一个新路径新文件新sheet页中
        try {
            ExcelHelper.mergeDirSheetsToNewOneSheet(sourceDirPath, outputFilePath, sourceSheetNames, -1, -1, true, skipEmptyCellOfColumn);
        } catch (IOException e) {
            logger.error("合并目录中多个Excel文件中的多个sheet页到一个新路径新文件新sheet页出错！", e);
        }


        Path searchedExcelPath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_THREE_PATH + "/table_name_target.xlsx");
        Path modifiedExcelPath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_SIX_PATH + "/slow_sql_pre_task.xlsx");
        // 调用ExcelHelper的通用方法
        ExcelHelper.excelVlookupUpdate(searchedExcelPath, null, 2, Arrays.asList(5, 6, 1), modifiedExcelPath, null, 9, Arrays.asList(10, 11, 12), false);


        // 增加一个处理类,处理数据
        Path slowSqlStepSixPath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_SIX_PATH);
        FileAccessor.traverseDirectory(new File(String.valueOf(slowSqlStepSixPath)), this::processStepSixFile);

    }

    public static void createZenPathTask(List<List<String>> data) {
        // 创建禅道任务文件
        String[] taskHeaders = {"序号", "所属执行", "任务类型", "指派给", "任务名称", "任务描述", "预计开始日期", "预计结束日期", "预计工时（小时）", "优先级（1-4）"};
        try (FileOutputStream fos = new FileOutputStream(String.valueOf(FileAccessor.getProjectRootFolderPath(TASK_LIST_FILE_EXCEL)))) {
            ExcelHelper.exportToExcel(taskHeaders, data, fos);
            logger.debug("Excel文件生成成功！");
        } catch (IOException e) {
            logger.debug("Excel文件生成失败：\r\n", e);
        }
    }

    public void processStepSixFile(File file) {
        logger.debug("正在处理: " + file.getAbsolutePath());
        if (!isExcelFile(Paths.get(file.getAbsolutePath()))) {
            return;
        }
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 加7天
        LocalDate futureDate = currentDate.plusDays(7);
        // 定义日期格式器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 格式化日期
        String formatCurrentDate = currentDate.format(formatter);
        // 格式化日期
        String formatFutureDate = futureDate.format(formatter);
        List<List<String>> sheetData = new ArrayList<>();
        try {
            Workbook workbook = ExcelHelper.readWorkbook(Paths.get(file.getAbsolutePath()));
            Sheet sheet = workbook.getSheet("result");
            for (int i = 1; i < sheet.getLastRowNum(); i++) {
                List<String> nativeValues = new ArrayList<>();
                List<String> finalValues = new ArrayList<>();
                Row row = sheet.getRow(i);
                if (row.getLastCellNum() < 13) {
                    continue;
                }
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    String cellValue = ExcelHelper.getCellValueAsString(row.getCell(j)).trim();
                    nativeValues.add(cellValue);
                }
                String serialNumber = "";
                String parentTask = "系统开发";
                String taskType = "开发";
                String assignedTo = nativeValues.get(10);
                String taskName = nativeValues.get(12) + "-性能优化-耗时：" + nativeValues.get(4) + ",最大返回行数：" + nativeValues.get(5) + ",序号：" + i;
                String taskDescription = "所属环境库：\r\n ---" + nativeValues.get(8) + "\r\n ---所属数据库：" + nativeValues.get(1)
                        + "\r\n ---慢SQL脚本：\r\n" + nativeValues.get(0)
                        + "\r\n ---SVN路径地址：98-工作区/09-开发组/01-开发实施组/08-评审管理/非功能优化";
                String estimatedStartDate = formatCurrentDate;
                String estimatedEndDate = formatFutureDate;
                String estimatedDurationHours = "8";
                String priority = "2";
                finalValues.add(serialNumber);
                finalValues.add(parentTask);
                finalValues.add(taskType);
                finalValues.add(assignedTo);
                finalValues.add(taskName);
                finalValues.add(taskDescription);
                finalValues.add(estimatedStartDate);
                finalValues.add(estimatedEndDate);
                finalValues.add(estimatedDurationHours);
                finalValues.add(priority);
                sheetData.add(finalValues);
            }
            createZenPathTask(sheetData);
        } catch (IOException e) {
            logger.debug(String.format("创建禅道任务失败!!!"), e);
        }
    }

    /**
     * Excel文件处理核心逻辑
     */
    public void processExcelsqlList(File file) {
        logger.debug("正在处理: " + file.getAbsolutePath());
        if (!isExcelFile(Paths.get(file.getAbsolutePath()))) {
            return;
        }
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {


            Sheet sheet = workbook.getSheet("sqlList");
            if (sheet == null) {
                logger.debug("[警告] 缺少sqlList工作表");
                return;
            }
            // 删除重复行
            ExcelHelper.removeDuplicateRowsByColumns(sheet, new int[]{0});

            // 添加表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                headerRow = sheet.createRow(0);
            }
            // 如果I列已有表头则不覆盖
            if (headerRow.getCell(8) == null) {
                headerRow.createCell(8).setCellValue("所属环境库");
            }
            // 填充文件名
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null) {
                    Cell cell = row.getCell(8);
                    if (cell == null) cell = row.createCell(8);
                    cell.setCellValue(file.getName());
                }
            }

            // 构建新目录路径
            Path stepFourSlowSqlPath = FileAccessor.getProjectRootFolderPath(SLOW_SQL_STEP_FOUR_PATH);
            try {
                Files.createDirectories(stepFourSlowSqlPath); // 确保目录存在
            } catch (IOException e) {
                logger.debug("目录创建失败: " + stepFourSlowSqlPath + " - " + e.getMessage());
                return;
            }

            // 构建新文件路径（保留原文件名）
            Path newFilePath = stepFourSlowSqlPath.resolve(file.getName());

            // 保存到新路径
            try (FileOutputStream fos = new FileOutputStream(newFilePath.toFile())) {
                workbook.write(fos);
                logger.debug("处理成功，文件已保存至: " + newFilePath);
            }

        } catch (IOException e) {
            logger.debug("处理失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * 删除B列值为oceanbase的行处理逻辑
     * 根据A列SQL语句提取表名到J列
     * 处理J列库.表的情况
     */
    private void processStepFiveFile(File file) {
        logger.debug("正在执行processStepFiveFile记录: " + file.getAbsolutePath());
        if (isExcelFile(Paths.get(file.getAbsolutePath()))) {
            try (FileInputStream fis = new FileInputStream(file);
                 Workbook workbook = new XSSFWorkbook(fis)) {

                Sheet sheet = workbook.getSheet("sqlList");
                if (sheet == null) {
                    logger.debug("[警告] 缺少sqlList工作表");
                    return;
                }

                // 设置J列表头
                Row headerRow = sheet.getRow(0);
                if (headerRow == null) {
                    headerRow = sheet.createRow(0);
                }
                Cell headerJ = headerRow.createCell(9);
                headerJ.setCellValue("表名称");

                // 逆序遍历避免删除导致的行索引错乱（从最后一行到第一行）
                for (int i = sheet.getLastRowNum(); i >= 1; i--) { // 从1开始跳过标题行
                    Row row = sheet.getRow(i);
                    if (row == null) continue;

                    // 处理B列删除逻辑
                    Cell cellB = row.getCell(1);
                    if (cellB != null) {
                        String cellBValue = ExcelHelper.getCellValueAsString(cellB).trim();
                        if ("oceanbase".equalsIgnoreCase(cellBValue)) {
                            sheet.removeRow(row);
                            logger.debug("[警告] 删除第" + i + "行:,B列值= " + cellBValue);
                            continue; // 删除后跳过后续处理
                        }
                    }

                    // 处理A列提取表名到J列
                    Cell cellA = row.getCell(0);
                    if (cellA == null) continue;

                    String sql = ExcelHelper.getCellValueAsString(cellA).trim();
                    if (sql.isEmpty()) continue;

                    // 提取表名并校验结果
                    String tableName = SqlTableNameConverter.getTableName(sql);
                    if (tableName == null || tableName.isEmpty()) {
                        logger.debug("[警告] 第" + i + "行无法提取表名: " + sql);
                        continue;
                    }
                    try {
                        if (tableName.indexOf(".") > 0) {
                            String[] tableNames = tableName.split("\\.");
                            tableName = tableNames[1];
                            logger.info(String.format(" └── 表名称：%s,表名数组：%s", tableName, JSONArray.toJSONString(tableNames)));
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        logger.error(sql, e);
                    }

                    Cell cellJ = row.createCell(9);
                    cellJ.setCellValue(tableName);
                    logger.debug("赋值第" + i + 1 + "行: J列值=" + tableName);
                }

                // 自动调整列宽（可选）
                sheet.autoSizeColumn(9);

                // 保存修改
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                    logger.debug("执行已完成，文件已更新");
                }
            } catch (Exception e) {
                logger.debug("处理失败: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // 执行测试方法
        SlowSqlTaskExtractor extractor = new SlowSqlTaskExtractor();
        extractor.createTaskBySlowSQLAnalyzer();
    }
}
