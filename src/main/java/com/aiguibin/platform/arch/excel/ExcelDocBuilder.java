package com.aiguibin.platform.arch.excel;

import com.aiguibin.platform.arch.dto.ColumnStructure;
import com.aiguibin.platform.arch.dto.IndexStructure;
import com.aiguibin.platform.arch.dto.TableStructure;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.common.usermodel.HyperlinkType;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExcelDocBuilder {
    private String filePath;

    public Workbook createWorkbook(String filePath) {
        this.filePath = filePath;
        return new XSSFWorkbook();
    }

    public void addChangeRecordSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("变更记录");

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("版本号");
        titleRow.createCell(1).setCellValue("变更日期");
        titleRow.createCell(2).setCellValue("变更人");
        titleRow.createCell(3).setCellValue("变更内容");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("1.0");
        dataRow.createCell(1).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dataRow.createCell(2).setCellValue("系统自动生成");
        dataRow.createCell(3).setCellValue("初始版本");

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
    }

    public void addTableOfContentsSheet(Workbook workbook, List<TableInfo> tableInfos) {
        Sheet sheet = workbook.createSheet("目录");

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("序号");
        titleRow.createCell(1).setCellValue("表名");
        titleRow.createCell(2).setCellValue("表注释");
        titleRow.createCell(3).setCellValue("链接");

        CreationHelper creationHelper = workbook.getCreationHelper();

        for (int i = 0; i < tableInfos.size(); i++) {
            TableInfo tableInfo = tableInfos.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(tableInfo.getTableName());
            row.createCell(2).setCellValue(tableInfo.getTableComment());

            Cell linkCell = row.createCell(3);
            linkCell.setCellValue("查看详情");

            // Create hyperlink using the correct HyperlinkType enum
            Hyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
            hyperlink.setAddress("#'" + tableInfo.getTableName() + "'!A1");
            linkCell.setHyperlink(hyperlink);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
    }

    public void addTableIndexSheet(Workbook workbook, List<TableInfo> tableInfos) {
        Sheet sheet = workbook.createSheet("表索引");

        Row titleRow = sheet.createRow(0);
        titleRow.createCell(0).setCellValue("序号");
        titleRow.createCell(1).setCellValue("表名");
        titleRow.createCell(2).setCellValue("表注释");

        for (int i = 0; i < tableInfos.size(); i++) {
            TableInfo tableInfo = tableInfos.get(i);
            Row row = sheet.createRow(i + 1);

            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(tableInfo.getTableName());
            row.createCell(2).setCellValue(tableInfo.getTableComment());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    public void addTableSheet(Workbook workbook, TableStructure tableStructure) {
        Sheet sheet = workbook.createSheet(tableStructure.getTableName());

        Row tableNameRow = sheet.createRow(0);
        Cell tableNameCell = tableNameRow.createCell(0);
        tableNameCell.setCellValue("表名：" + tableStructure.getTableName());

        Row tableCommentRow = sheet.createRow(1);
        Cell tableCommentCell = tableCommentRow.createCell(0);
        tableCommentCell.setCellValue("表注释：" + tableStructure.getTableComment());

        Row columnTitleRow = sheet.createRow(3);
        columnTitleRow.createCell(0).setCellValue("序号");
        columnTitleRow.createCell(1).setCellValue("字段名");
        columnTitleRow.createCell(2).setCellValue("字段类型");
        columnTitleRow.createCell(3).setCellValue("字段长度");
        columnTitleRow.createCell(4).setCellValue("是否为空");
        columnTitleRow.createCell(5).setCellValue("字段注释");

        List<ColumnStructure> columns = tableStructure.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            ColumnStructure column = columns.get(i);
            Row row = sheet.createRow(i + 4);

            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(column.getColumnName());
            row.createCell(2).setCellValue(column.getColumnType());
            row.createCell(3).setCellValue(column.getColumnLength());
            row.createCell(4).setCellValue(column.isNullable() ? "是" : "否");
            row.createCell(5).setCellValue(column.getColumnComment());
        }

        if (!tableStructure.getIndexes().isEmpty()) {
            int indexStartRow = columns.size() + 6;
            Row indexTitleRow = sheet.createRow(indexStartRow);
            indexTitleRow.createCell(0).setCellValue("索引名称");
            indexTitleRow.createCell(1).setCellValue("索引类型");
            indexTitleRow.createCell(2).setCellValue("索引列");

            List<IndexStructure> indexes = tableStructure.getIndexes();
            for (int i = 0; i < indexes.size(); i++) {
                IndexStructure index = indexes.get(i);
                Row row = sheet.createRow(indexStartRow + i + 1);

                row.createCell(0).setCellValue(index.getIndexName());
                row.createCell(1).setCellValue(index.isUnique() ? "唯一索引" : "普通索引");
                row.createCell(2).setCellValue(String.join(", ", index.getColumns()));
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
    }

    public void saveWorkbook(Workbook workbook) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        } finally {
            workbook.close();
        }
    }
}