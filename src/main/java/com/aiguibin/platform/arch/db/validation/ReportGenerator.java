package com.aiguibin.platform.arch.db.validation;

import com.aiguibin.platform.arch.vo.DiffInfo;
import com.aiguibin.platform.arch.vo.ValidationResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ReportGenerator {

    // 生成差异报告
    public byte[] generateReport(ValidationResult result) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("差异报告");
        
        // 创建表头
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("差异类型");
        headerRow.createCell(1).setCellValue("表名");
        headerRow.createCell(2).setCellValue("字段名");
        headerRow.createCell(3).setCellValue("文档值");
        headerRow.createCell(4).setCellValue("数据库值");
        headerRow.createCell(5).setCellValue("描述");
        headerRow.createCell(6).setCellValue("修复脚本");
        
        // 设置表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            headerRow.getCell(i).setCellStyle(headerStyle);
        }
        
        // 填充数据
        List<DiffInfo> diffs = result.getDiffs();
        for (int i = 0; i < diffs.size(); i++) {
            DiffInfo diff = diffs.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(diff.getDiffType());
            row.createCell(1).setCellValue(diff.getTableName());
            row.createCell(2).setCellValue(diff.getColumnName());
            row.createCell(3).setCellValue(diff.getDocValue());
            row.createCell(4).setCellValue(diff.getDbValue());
            row.createCell(5).setCellValue(diff.getDescription());
            row.createCell(6).setCellValue(diff.getFixScript());
        }
        
        // 自动调整列宽
        for (int i = 0; i < 7; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 写入输出流
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();
        
        return baos.toByteArray();
    }
}