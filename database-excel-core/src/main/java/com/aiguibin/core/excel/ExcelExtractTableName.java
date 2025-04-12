package com.aiguibin.core.excel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 慢SQL梳理第二步，提取慢SQL中的表名
 */
public class ExcelExtractTableName {

    public static void main(String[] args) throws Exception {
        // 1. 读取Excel文件
        FileInputStream file = new FileInputStream("F:\\Desktop\\checkedExcel\\slow_sql.xlsx");
        Workbook workbook = new XSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0); // 获取第一个Sheet

        // 正则表达式匹配第一个FROM后的表名（不区分大小写）
        Pattern pattern = Pattern.compile(
                "(?i)\\bFROM\\s+([^\\s(]+)", // 忽略子查询中的FROM
                Pattern.CASE_INSENSITIVE | Pattern.DOTALL
        );

        // 2. 遍历每一行（跳过标题行）
        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // 跳过标题行

            // 3. 获取A列（SQL文本）
            Cell sqlCell = row.getCell(0); // A列是第0列
            if (sqlCell == null) continue;

            String sql = sqlCell.getStringCellValue()
                    .replaceAll("<br>", " ") // 清理换行符
                    .replaceAll("\\s+", " "); // 合并空格

            // 4. 提取表名
            Matcher matcher = pattern.matcher(sql);
            if (matcher.find()) {
                String tableName = matcher.group(1);
                // 去掉可能的别名（如"table AS t" -> "table"）
                tableName = tableName.split("\\s+")[0];
                // 去掉模式前缀（如"database.table" -> "table"）
                tableName = tableName.replaceAll(".*\\.", "");

                // 5. 写入I列（第7列）
                Cell resultCell = row.createCell(8); // I列是第8列
                resultCell.setCellValue(tableName);
            }
        }

        // 6. 保存修改后的文件
        FileOutputStream outFile = new FileOutputStream("F:\\Desktop\\checkedExcel\\slow_sql_updated.xlsx");
        workbook.write(outFile);
        workbook.close();
        outFile.close();
    }
}