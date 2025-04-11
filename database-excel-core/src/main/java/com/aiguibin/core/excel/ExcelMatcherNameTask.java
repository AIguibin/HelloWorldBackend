package com.aiguibin.core.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 慢SQL第三步，根据表名总表提取表所对应的能力中心
 */
public class ExcelMatcherNameTask {


    public static void main(String[] args) {
        // 文件路径
        String slowSqlFilePath = "F:\\Desktop\\checkedExcel\\slow_sql_updated.xlsx";
        String tableListFilePath = "F:\\Desktop\\checkedExcel\\tablelist_for_slow_sql.xlsx";
        String outputFilePath = "F:\\Desktop\\checkedExcel\\slow_sql_updated_task.xlsx";

        // 创建映射表
        Map<String, String> tableToServiceMap = new HashMap<>();

        try (
                FileInputStream tableListFile = new FileInputStream(tableListFilePath);
                FileInputStream slowSqlFile = new FileInputStream(slowSqlFilePath);
                XSSFWorkbook tableListWorkbook = new XSSFWorkbook(tableListFile);
                XSSFWorkbook slowSqlWorkbook = new XSSFWorkbook(slowSqlFile);
                FileOutputStream outputStream = new FileOutputStream(outputFilePath)
        ) {
            // 读取 tablelist_for_slow_sql.xlsx 文件
            Sheet tableListSheet = tableListWorkbook.getSheetAt(0);
            for (Row row : tableListSheet) {
                // 假设表名在 C 列，微服务中心在 B 列
                String tableName = getStringCellValue(row.getCell(2));
                String serviceName = getStringCellValue(row.getCell(1));
                if (tableName != null && serviceName != null) {
                    tableToServiceMap.put(tableName, serviceName);
                }
            }

            // 读取 slow_sql_updated.xlsx 文件
            Sheet slowSqlSheet = slowSqlWorkbook.getSheetAt(0);

            // 创建新的 J 列标题
            Row headerRow = slowSqlSheet.getRow(0);
            if (headerRow == null) {
                headerRow = slowSqlSheet.createRow(0);
            }
            headerRow.createCell(9).setCellValue("微服务中心"); // J 列索引为 9

            // 遍历每一行，填充 J 列
            for (int rowIndex = 1; rowIndex <= slowSqlSheet.getLastRowNum(); rowIndex++) {
                Row row = slowSqlSheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }

                // 获取 I 列的表名（I 列索引为 8）
                String tableName = getStringCellValue(row.getCell(8));
                if (tableName != null) {
                    String serviceName = tableToServiceMap.get(tableName);
                    if (serviceName != null) {
                        row.createCell(9).setCellValue(serviceName); // J 列索引为 9
                    }
                }
            }

            // 保存结果到新的文件
            slowSqlWorkbook.write(outputStream);
            System.out.println("任务完成，结果已保存到 " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取单元格的字符串值
    private static String getStringCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}