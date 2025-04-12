package com.aiguibin.core.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 递归把所有指定的多个相同结构Sheet合并成一个sheet页
 * 慢SQL梳理第一步分周合并成一个文件
 */
public class ExcelSheetMergeByIndex {


    public static void main(String[] args) {
        String baseDir = "F:\\Desktop\\非功能优化\\慢sql性能优化";
        String outputPath = "F:\\Desktop\\checkedExcel\\slow_sql.xlsx";

        List<File> excelFiles = new ArrayList<>();
        collectExcelFiles(new File(baseDir), excelFiles);

        List<List<String>> mergedData = new ArrayList<>();
        DataFormatter formatter = new DataFormatter();

        // 读取所有Excel的第一个Sheet数据
        for (File file : excelFiles) {
            try (Workbook workbook = WorkbookFactory.create(file)) {
                if (workbook.getNumberOfSheets() == 0) {
                    System.err.println("文件无Sheet页: " + file.getPath());
                    continue;
                }
                Sheet sheet = workbook.getSheetAt(0);
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
