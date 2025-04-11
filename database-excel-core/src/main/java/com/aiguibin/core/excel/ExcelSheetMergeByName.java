package com.aiguibin.core.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 递归把所有指定的多个相同结构Sheet合并成一个sheet页
 */
public class ExcelSheetMergeByName {


    public static void main(String[] args) {
        String baseDir = "F:\\Desktop\\AD-天阳架构实施之数据库设计";
        String outputPath = baseDir + File.separator + "database.xlsx";

        List<File> excelFiles = new ArrayList<>();
        collectExcelFiles(new File(baseDir), excelFiles);

        List<List<String>> mergedData = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        // 读取所有Excel中名为“目录”的Sheet数据
        for (File file : excelFiles) {
            try (Workbook workbook = WorkbookFactory.create(file)) {
                // 按名称获取Sheet
                Sheet sheet = workbook.getSheet("目录");

                // 双重检查：1.文件是否有Sheet 2.是否有名为"目录"的Sheet
                if (sheet == null) {
                    System.err.println("文件无'目录'Sheet页: " + file.getPath());
                    continue; // 跳过没有"目录"的文件
                }

                // 读取数据
                for (Row row : sheet) {
                    List<String> rowData = new ArrayList<>();
                    for (Cell cell : row) {
                        rowData.add(formatter.formatCellValue(cell));
                    }
                    mergedData.add(rowData);
                }
            } catch (Exception e) {
                System.err.println("处理文件失败: " + file.getPath());
                e.printStackTrace();
            }
        }

        // 写入合并后的Excel
        try (Workbook outputWorkbook = new XSSFWorkbook()) {
            Sheet outputSheet = outputWorkbook.createSheet("Sheet1");
            int rowIdx = 0;
            for (List<String> row : mergedData) {
                Row outputRow = outputSheet.createRow(rowIdx++);
                int colIdx = 0;
                for (String value : row) {
                    outputRow.createCell(colIdx++).setCellValue(value);
                }
            }

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                outputWorkbook.write(fos);
                System.out.println("文件已保存至: " + outputPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 递归收集Excel文件
    private static void collectExcelFiles(File dir, List<File> excelFiles) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                collectExcelFiles(file, excelFiles);
            } else {
                String name = file.getName().toLowerCase();
                if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
                    excelFiles.add(file);
                }
            }
        }
    }
}
