package com.aiguibin.core.excel;



import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 递归把所有指定的Sheet汇总到一个Excel多个sheet页
 */

public class ExcelSheetMergeForWork {

    public static void main(String[] args) {
        String baseDir = "F:\\Desktop\\非功能优化\\慢sql性能优化";
        String targetPath = baseDir + "\\slow_sql.xlsx";

        try {
            // 收集所有Excel文件
            List<File> excelFiles = new ArrayList<>();
            collectExcelFiles(new File(baseDir), excelFiles);

            // 创建目标工作簿
            try (XSSFWorkbook targetWorkbook = new XSSFWorkbook()) {
                // 处理每个Excel文件
                for (File file : excelFiles) {
                    processFile(file, targetWorkbook);
                }

                // 保存目标文件
                try (FileOutputStream fos = new FileOutputStream(targetPath)) {
                    targetWorkbook.write(fos);
                }
                System.out.println("合并完成，文件保存至：" + targetPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void collectExcelFiles(File dir, List<File> excelFiles) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                collectExcelFiles(file, excelFiles);
            } else if (isExcelFile(file)) {
                excelFiles.add(file);
            }
        }
    }

    private static boolean isExcelFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".xls") || name.endsWith(".xlsx");
    }

    private static void processFile(File file, Workbook targetWorkbook) {
        try (InputStream is = new FileInputStream(file);
             Workbook sourceWorkbook = WorkbookFactory.create(is)) {

            Sheet sourceSheet = sourceWorkbook.getSheetAt(0);
            if (sourceSheet == null) return;

            // 创建新Sheet并复制数据
            String sheetName = generateUniqueSheetName(targetWorkbook, file.getName());
            Sheet targetSheet = targetWorkbook.createSheet(sheetName);
            copySheet(sourceSheet, targetSheet);

        } catch (Exception e) {
            System.err.println("处理文件失败：" + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private static String generateUniqueSheetName(Workbook workbook, String fileName) {
        // 清理文件名作为sheet名称
        String baseName = fileName.replaceFirst("[.][xX][lL][sSxX]$", "")
                .replaceAll("[\\\\/:*?\\[\\]]", "_");

        if (baseName.length() > 31) {
            baseName = baseName.substring(0, 31);
        }

        String finalName = baseName;
        int counter = 1;
        while (workbook.getSheet(finalName) != null) {
            finalName = baseName + "_" + counter;
            if (finalName.length() > 31) {
                finalName = finalName.substring(0, 31 - String.valueOf(counter).length() - 1) + "_" + counter;
            }
            counter++;
        }
        return finalName;
    }

    private static void copySheet(Sheet source, Sheet target) {
        for (int i = 0; i <= source.getLastRowNum(); i++) {
            Row sourceRow = source.getRow(i);
            if (sourceRow == null) continue;

            Row targetRow = target.createRow(i);
            copyRow(sourceRow, targetRow);
        }
    }

    private static void copyRow(Row sourceRow, Row targetRow) {
        for (int j = 0; j < sourceRow.getLastCellNum(); j++) {
            Cell sourceCell = sourceRow.getCell(j);
            if (sourceCell == null) continue;

            Cell targetCell = targetRow.createCell(j);
            copyCellValue(sourceCell, targetCell);
        }
    }

    private static void copyCellValue(Cell source, Cell target) {
        CellType cellType = source.getCellType();
        switch (cellType) {
            case STRING:
                target.setCellValue(source.getStringCellValue());
                break;
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(source)) {
                    target.setCellValue(source.getDateCellValue());
                } else {
                    target.setCellValue(source.getNumericCellValue());
                }
                break;
            case BOOLEAN:
                target.setCellValue(source.getBooleanCellValue());
                break;
            case FORMULA:
                target.setCellFormula(source.getCellFormula());
                break;
            case BLANK:
                target.setBlank();
                break;
            default:
                target.setCellValue("");
        }
    }
}