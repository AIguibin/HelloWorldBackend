package com.aiguibin.platform.arch.db.validation;

import com.aiguibin.platform.arch.dto.ColumnStructure;
import com.aiguibin.platform.arch.dto.IndexStructure;
import com.aiguibin.platform.arch.dto.TableStructure;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DocParser {

    // 解析 Excel 格式的表结构文档
    public Map<String, TableStructure> parseExcel(File file) {
        Map<String, TableStructure> tableStructures = new HashMap<>();
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(fis)) {
            
            // 解析目录 sheet，获取所有表名
            Sheet catalogSheet = workbook.getSheet("目录");
            if (catalogSheet == null) {
                throw new IOException("Excel 文件中缺少 '目录' sheet");
            }
            
            // 遍历目录 sheet 中的每行数据
            for (int i = 1; i <= catalogSheet.getLastRowNum(); i++) {
                Row row = catalogSheet.getRow(i);
                if (row != null) {
                    Cell tableNameCell = row.getCell(2); // 表名
                    Cell tableNameZhCell = row.getCell(3); // 表名中文
                    
                    if (tableNameCell != null && tableNameZhCell != null) {
                        String tableName = tableNameCell.getStringCellValue().trim();
                        String tableNameZh = tableNameZhCell.getStringCellValue().trim();
                        
                        if (!tableName.isEmpty() && !tableNameZh.isEmpty()) {
                            // 解析单个表的结构
                            Sheet tableSheet = workbook.getSheet(tableNameZh);
                            if (tableSheet != null) {
                                TableStructure tableStructure = parseTableSheet(tableSheet, tableName, tableNameZh);
                                tableStructures.put(tableName, tableStructure);
                            }
                        }
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return tableStructures;
    }

    // 解析单个表的结构
    private TableStructure parseTableSheet(Sheet sheet, String tableName, String tableNameZh) {
        TableStructure tableStructure = new TableStructure();
        tableStructure.setTableName(tableName);
        tableStructure.setTableComment(tableNameZh);
        
        List<ColumnStructure> columns = new ArrayList<>();
        List<IndexStructure> indexes = new ArrayList<>();
        
        // 解析字段信息
        boolean isColumnSection = false;
        boolean isIndexSection = false;
        
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            
            Cell firstCell = row.getCell(0);
            if (firstCell != null) {
                String cellValue = firstCell.getStringCellValue().trim();
                
                // 识别字段部分
                if (cellValue.equals("字段名")) {
                    isColumnSection = true;
                    isIndexSection = false;
                    continue;
                }
                
                // 识别索引部分
                if (cellValue.equals("索引名")) {
                    isColumnSection = false;
                    isIndexSection = true;
                    continue;
                }
            }
            
            // 解析字段信息
            if (isColumnSection) {
                ColumnStructure column = parseColumn(row);
                if (column != null) {
                    columns.add(column);
                }
            }
            
            // 解析索引信息
            if (isIndexSection) {
                IndexStructure index = parseIndex(row);
                if (index != null) {
                    indexes.add(index);
                }
            }
        }
        
        tableStructure.setColumns(columns);
        tableStructure.setIndexes(indexes);
        
        return tableStructure;
    }

    // 解析字段信息
    private ColumnStructure parseColumn(Row row) {
        ColumnStructure column = new ColumnStructure();
        
        try {
            // 字段名
            Cell columnNameCell = row.getCell(0);
            if (columnNameCell == null || columnNameCell.getStringCellValue().trim().isEmpty()) {
                return null;
            }
            column.setColumnName(columnNameCell.getStringCellValue().trim());
            
            // 字段类型
            Cell columnTypeCell = row.getCell(1);
            if (columnTypeCell != null) {
                String columnType = columnTypeCell.getStringCellValue().trim();
                column.setColumnType(columnType);
                
                // 提取字段长度
                int length = extractLength(columnType);
                column.setColumnLength(length);
            }
            
            // 字段注释
            Cell columnCommentCell = row.getCell(2);
            if (columnCommentCell != null) {
                column.setColumnComment(columnCommentCell.getStringCellValue().trim());
            }
            
            // 是否可为空
            Cell nullableCell = row.getCell(3);
            if (nullableCell != null) {
                String nullableStr = nullableCell.getStringCellValue().trim();
                column.setNullable("是".equals(nullableStr) || "YES".equals(nullableStr.toUpperCase()));
            }
            
            // 默认值
            Cell defaultValueCell = row.getCell(4);
            if (defaultValueCell != null) {
                column.setDefaultValue(defaultValueCell.getStringCellValue().trim());
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return column;
    }

    // 解析索引信息
    private IndexStructure parseIndex(Row row) {
        IndexStructure index = new IndexStructure();
        
        try {
            // 索引名
            Cell indexNameCell = row.getCell(0);
            if (indexNameCell == null || indexNameCell.getStringCellValue().trim().isEmpty()) {
                return null;
            }
            index.setIndexName(indexNameCell.getStringCellValue().trim());
            
            // 索引列
            Cell columnsCell = row.getCell(1);
            if (columnsCell != null) {
                String columnsStr = columnsCell.getStringCellValue().trim();
                List<String> columns = new ArrayList<>();
                for (String column : columnsStr.split("，")) {
                    columns.add(column.trim());
                }
                index.setColumns(columns);
            }
            
            // 是否唯一索引
            Cell uniqueCell = row.getCell(2);
            if (uniqueCell != null) {
                String uniqueStr = uniqueCell.getStringCellValue().trim();
                index.setUnique("是".equals(uniqueStr) || "YES".equals(uniqueStr.toUpperCase()) || "UNIQUE".equals(uniqueStr.toUpperCase()));
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        
        return index;
    }

    // 从字段类型中提取长度
    private int extractLength(String columnType) {
        int length = 0;
        
        // 查找括号中的数字
        int startIndex = columnType.indexOf("(");
        int endIndex = columnType.indexOf(")");
        
        if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
            try {
                String lengthStr = columnType.substring(startIndex + 1, endIndex);
                // 处理类似 "255,0" 的情况
                if (lengthStr.contains(",")) {
                    lengthStr = lengthStr.split(",")[0];
                }
                length = Integer.parseInt(lengthStr);
            } catch (NumberFormatException e) {
                // 解析失败，返回 0
            }
        }
        
        return length;
    }
}
