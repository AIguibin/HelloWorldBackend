package com.aiguibin.core.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 递归遍历数据库设计文件夹下各组的Excel，取到目录SHEET页
 * 并把内容合并到一个SHEET页--表英文名+中文名+服务中心，为慢SQL提供任务分组
 */

public class ExcelDbSheetMerger {

    private static final String TARGET_SHEET_NAME = "目录";
    private static final String TARGET_FILE_NAME = "database_table.xlsx";

    public static void main(String[] args) {
        String rootPath = "F:\\Desktop\\AD-天阳架构实施之数据库设计";
        File rootDir = new File("F:\\Desktop\\AD-天阳架构实施之数据库设计");
        File targetFile = new File(rootDir, TARGET_FILE_NAME);

        // 删除旧文件
        if (targetFile.exists() && !targetFile.delete()) {
            System.err.println("无法删除旧文件: " + targetFile.getAbsolutePath());
            return;
        }

        try (Workbook mergedWorkbook = new XSSFWorkbook()) {
            Sheet mergedSheet = mergedWorkbook.createSheet(TARGET_SHEET_NAME);
            int[] rowCounter = {0};

            processDirectory(rootDir, mergedWorkbook, mergedSheet, rowCounter);

            try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                mergedWorkbook.write(fos);
            }
            System.out.println("处理完成，结果已保存至：" + targetFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processDirectory(File dir, Workbook mergedWorkbook, Sheet mergedSheet, int[] rowCounter) {
        File[] files = dir.listFiles();
        if (files == null) {
            System.err.println("目录访问失败: " + dir.getAbsolutePath());
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file, mergedWorkbook, mergedSheet, rowCounter);
            } else if (isExcelFile(file)) {
                System.out.println("正在处理: " + file.getAbsolutePath()); // 添加处理日志
                processExcelFile(file, mergedWorkbook, mergedSheet, rowCounter);
            }
        }
    }

    private static boolean isExcelFile(File file) {
        String name = file.getName().toLowerCase();
        return name.endsWith(".xls") || name.endsWith(".xlsx");
    }

    private static void processExcelFile(File file, Workbook mergedWorkbook, Sheet mergedSheet, int[] rowCounter) {
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sourceSheet = workbook.getSheet(TARGET_SHEET_NAME);
            if (sourceSheet == null) {
                System.out.println("跳过无目录Sheet的文件: " + file.getName());
                return;
            }

            // 处理合并区域
            for (int i = 0; i < sourceSheet.getNumMergedRegions(); i++) {
                CellRangeAddress mergedRegion = sourceSheet.getMergedRegion(i);
                int firstRow = mergedRegion.getFirstRow();
                int lastRow = mergedRegion.getLastRow();
                int firstCol = mergedRegion.getFirstColumn();
                int lastCol = mergedRegion.getLastColumn();

                // 跳过完全不在前四列的合并区域
                if (lastCol < 0 || firstCol > 3) continue;

                // 调整列范围为前四列
                int adjustedFirstCol = Math.max(firstCol, 0);
                int adjustedLastCol = Math.min(lastCol, 3);

                // 跳过无效范围
                if (adjustedFirstCol > adjustedLastCol) continue;

                // 计算调整后的行号（关键修正：直接累加当前行号）
                int adjustedFirstRow = firstRow + rowCounter[0];
                int adjustedLastRow = lastRow + rowCounter[0];

                // 防止负数行号（如合并区域在标题行之前）
                adjustedFirstRow = Math.max(adjustedFirstRow, 0);
                adjustedLastRow = Math.max(adjustedLastRow, 0);

                CellRangeAddress adjustedMergedRegion =
                        new CellRangeAddress(adjustedFirstRow, adjustedLastRow, adjustedFirstCol, adjustedLastCol);

                mergedSheet.addMergedRegion(adjustedMergedRegion);
            }

            // 处理数据行
            for (Row sourceRow : sourceSheet) {
                Row targetRow = mergedSheet.createRow(rowCounter[0]++);
                copyRow(sourceRow, targetRow, mergedWorkbook);
            }

        } catch (Exception e) {
            System.err.println("文件处理失败: " + file.getAbsolutePath());
            e.printStackTrace();
        }
    }

    private static void copyRow(Row sourceRow, Row targetRow, Workbook targetWorkbook) {
        targetRow.setHeight(sourceRow.getHeight());
        // 只复制前四列（0到3）
        for (int i = 0; i < 4; i++) {
            Cell sourceCell = sourceRow.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            Cell targetCell = targetRow.createCell(i);
            copyCell(sourceCell, targetCell, targetWorkbook);
        }
    }

    private static void copyCell(Cell sourceCell, Cell targetCell, Workbook targetWorkbook) {
        // 创建新样式（仅处理XSSF目标工作簿）
        CellStyle newStyle = targetWorkbook.createCellStyle();

        // 仅当源样式是XSSF时克隆完整样式
        if (sourceCell.getCellStyle() instanceof XSSFCellStyle) {
            newStyle.cloneStyleFrom(sourceCell.getCellStyle());
        } else {
            // 处理HSSF样式到XSSF的转换（基础属性）
            CellStyle sourceStyle = sourceCell.getCellStyle();

            // 字体克隆
            Font sourceFont = targetWorkbook.getFontAt(sourceStyle.getFontIndex());
            Font newFont = targetWorkbook.createFont();
            newFont.setFontName(sourceFont.getFontName());
            newFont.setFontHeightInPoints(sourceFont.getFontHeightInPoints());
            newFont.setColor(sourceFont.getColor());
            newStyle.setFont(newFont);

            // 基础格式
            newStyle.setDataFormat(targetWorkbook.createDataFormat().getFormat(sourceStyle.getDataFormatString()));

            // 对齐方式
            newStyle.setAlignment(sourceStyle.getAlignment());
            newStyle.setVerticalAlignment(sourceStyle.getVerticalAlignment());

            // 边框
            newStyle.setBorderTop(sourceStyle.getBorderTop());
            newStyle.setBorderBottom(sourceStyle.getBorderBottom());
            newStyle.setBorderLeft(sourceStyle.getBorderLeft());
            newStyle.setBorderRight(sourceStyle.getBorderRight());
        }
        targetCell.setCellStyle(newStyle);

        // 复制单元格值
        switch (sourceCell.getCellType()) {
            case STRING:
                handleStringCell(sourceCell, targetCell);
                break;
            case NUMERIC:
                handleNumericCell(sourceCell, targetCell);
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                targetCell.setCellFormula(sourceCell.getCellFormula());
                break;
            case ERROR:
                targetCell.setCellErrorValue(sourceCell.getErrorCellValue());
                break;
            default:
                targetCell.setCellValue("");
        }
    }

    private static void handleStringCell(Cell sourceCell, Cell targetCell) {
        String value = sourceCell.getStringCellValue();
        // 处理特殊字符转义
        if (value.contains("\"")) {
            value = value.replace("\"", "\"\"");
        }
        targetCell.setCellValue(value);
    }

    private static void handleNumericCell(Cell sourceCell, Cell targetCell) {
        if (DateUtil.isCellDateFormatted(sourceCell)) {
            targetCell.setCellValue(sourceCell.getDateCellValue());
        } else {
            targetCell.setCellValue(sourceCell.getNumericCellValue());
        }
    }
}