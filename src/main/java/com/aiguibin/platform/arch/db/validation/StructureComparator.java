package com.aiguibin.platform.arch.db.validation;

import com.aiguibin.platform.arch.dto.ColumnStructure;
import com.aiguibin.platform.arch.dto.DiffInfo;
import com.aiguibin.platform.arch.dto.IndexStructure;
import com.aiguibin.platform.arch.dto.TableStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureComparator {

    // 比对表结构
    public List<DiffInfo> compare(Map<String, TableStructure> docStructures, Map<String, TableStructure> dbStructures) {
        List<DiffInfo> diffs = new ArrayList<>();
        
        // 1. 比对表级差异
        compareTables(docStructures, dbStructures, diffs);
        
        // 2. 比对字段级差异
        compareColumns(docStructures, dbStructures, diffs);
        
        // 3. 比对索引级差异
        compareIndexes(docStructures, dbStructures, diffs);
        
        return diffs;
    }

    // 比对表级差异
    private void compareTables(Map<String, TableStructure> docStructures, Map<String, TableStructure> dbStructures, List<DiffInfo> diffs) {
        // 检查文档中的表是否在数据库中存在
        for (Map.Entry<String, TableStructure> docEntry : docStructures.entrySet()) {
            String tableName = docEntry.getKey();
            TableStructure docTable = docEntry.getValue();
            
            if (!dbStructures.containsKey(tableName)) {
                // 文档中的表在数据库中不存在
                DiffInfo diff = new DiffInfo();
                diff.setDiffType("TABLE_MISSING");
                diff.setTableName(tableName);
                diff.setDocValue(tableName + " (" + docTable.getTableComment() + ")");
                diff.setDbValue("不存在");
                diff.setDescription("数据库中缺少表：" + tableName);
                diff.setFixScript(generateCreateTableScript(docTable));
                diffs.add(diff);
            }
        }
        
        // 检查数据库中的表是否在文档中存在
        for (Map.Entry<String, TableStructure> dbEntry : dbStructures.entrySet()) {
            String tableName = dbEntry.getKey();
            
            if (!docStructures.containsKey(tableName)) {
                // 数据库中的表在文档中不存在
                DiffInfo diff = new DiffInfo();
                diff.setDiffType("TABLE_EXTRA");
                diff.setTableName(tableName);
                diff.setDocValue("不存在");
                diff.setDbValue(tableName);
                diff.setDescription("文档中缺少表：" + tableName);
                diff.setFixScript(""); // 不自动生成删除表的脚本，避免误操作
                diffs.add(diff);
            }
        }
    }

    // 比对字段级差异
    private void compareColumns(Map<String, TableStructure> docStructures, Map<String, TableStructure> dbStructures, List<DiffInfo> diffs) {
        // 遍历文档中的每个表
        for (Map.Entry<String, TableStructure> docEntry : docStructures.entrySet()) {
            String tableName = docEntry.getKey();
            TableStructure docTable = docEntry.getValue();
            
            // 检查数据库中是否存在该表
            if (dbStructures.containsKey(tableName)) {
                TableStructure dbTable = dbStructures.get(tableName);
                
                // 构建字段映射，方便比对
                Map<String, ColumnStructure> docColumns = new HashMap<>();
                for (ColumnStructure column : docTable.getColumns()) {
                    docColumns.put(column.getColumnName(), column);
                }
                
                Map<String, ColumnStructure> dbColumns = new HashMap<>();
                for (ColumnStructure column : dbTable.getColumns()) {
                    dbColumns.put(column.getColumnName(), column);
                }
                
                // 检查文档中的字段是否在数据库中存在
                for (Map.Entry<String, ColumnStructure> docColumnEntry : docColumns.entrySet()) {
                    String columnName = docColumnEntry.getKey();
                    ColumnStructure docColumn = docColumnEntry.getValue();
                    
                    if (!dbColumns.containsKey(columnName)) {
                        // 文档中的字段在数据库中不存在
                        DiffInfo diff = new DiffInfo();
                        diff.setDiffType("COLUMN_MISSING");
                        diff.setTableName(tableName);
                        diff.setColumnName(columnName);
                        diff.setDocValue(docColumn.getColumnType() + " COMMENT '" + docColumn.getColumnComment() + "'");
                        diff.setDbValue("不存在");
                        diff.setDescription("数据库表缺少字段：" + columnName);
                        diff.setFixScript(generateAddColumnScript(tableName, docColumn));
                        diffs.add(diff);
                    } else {
                        // 字段存在，比对字段属性
                        ColumnStructure dbColumn = dbColumns.get(columnName);
                        
                        // 比对字段类型
                        if (!docColumn.getColumnType().equalsIgnoreCase(dbColumn.getColumnType())) {
                            DiffInfo diff = new DiffInfo();
                            diff.setDiffType("COLUMN_TYPE_MISMATCH");
                            diff.setTableName(tableName);
                            diff.setColumnName(columnName);
                            diff.setDocValue(docColumn.getColumnType());
                            diff.setDbValue(dbColumn.getColumnType());
                            diff.setDescription("字段类型不匹配：" + columnName);
                            diff.setFixScript(generateModifyColumnScript(tableName, docColumn));
                            diffs.add(diff);
                        }
                        
                        // 比对字段注释
                        if (!docColumn.getColumnComment().equals(dbColumn.getColumnComment())) {
                            DiffInfo diff = new DiffInfo();
                            diff.setDiffType("COLUMN_COMMENT_MISMATCH");
                            diff.setTableName(tableName);
                            diff.setColumnName(columnName);
                            diff.setDocValue(docColumn.getColumnComment());
                            diff.setDbValue(dbColumn.getColumnComment());
                            diff.setDescription("字段注释不匹配：" + columnName);
                            diff.setFixScript(generateModifyColumnCommentScript(tableName, columnName, docColumn.getColumnComment()));
                            diffs.add(diff);
                        }
                    }
                }
                
                // 检查数据库中的字段是否在文档中存在
                for (Map.Entry<String, ColumnStructure> dbColumnEntry : dbColumns.entrySet()) {
                    String columnName = dbColumnEntry.getKey();
                    
                    if (!docColumns.containsKey(columnName)) {
                        // 数据库中的字段在文档中不存在
                        ColumnStructure dbColumn = dbColumnEntry.getValue();
                        DiffInfo diff = new DiffInfo();
                        diff.setDiffType("COLUMN_EXTRA");
                        diff.setTableName(tableName);
                        diff.setColumnName(columnName);
                        diff.setDocValue("不存在");
                        diff.setDbValue(dbColumn.getColumnType() + " COMMENT '" + dbColumn.getColumnComment() + "'");
                        diff.setDescription("文档中缺少字段：" + columnName);
                        diff.setFixScript(""); // 不自动生成删除字段的脚本，避免误操作
                        diffs.add(diff);
                    }
                }
            }
        }
    }

    // 比对索引级差异
    private void compareIndexes(Map<String, TableStructure> docStructures, Map<String, TableStructure> dbStructures, List<DiffInfo> diffs) {
        // 遍历文档中的每个表
        for (Map.Entry<String, TableStructure> docEntry : docStructures.entrySet()) {
            String tableName = docEntry.getKey();
            TableStructure docTable = docEntry.getValue();
            
            // 检查数据库中是否存在该表
            if (dbStructures.containsKey(tableName)) {
                TableStructure dbTable = dbStructures.get(tableName);
                
                // 构建索引映射，方便比对
                Map<String, IndexStructure> docIndexes = new HashMap<>();
                for (IndexStructure index : docTable.getIndexes()) {
                    docIndexes.put(index.getIndexName(), index);
                }
                
                Map<String, IndexStructure> dbIndexes = new HashMap<>();
                for (IndexStructure index : dbTable.getIndexes()) {
                    dbIndexes.put(index.getIndexName(), index);
                }
                
                // 检查文档中的索引是否在数据库中存在
                for (Map.Entry<String, IndexStructure> docIndexEntry : docIndexes.entrySet()) {
                    String indexName = docIndexEntry.getKey();
                    IndexStructure docIndex = docIndexEntry.getValue();
                    
                    if (!dbIndexes.containsKey(indexName)) {
                        // 文档中的索引在数据库中不存在
                        DiffInfo diff = new DiffInfo();
                        diff.setDiffType("INDEX_MISSING");
                        diff.setTableName(tableName);
                        diff.setColumnName(indexName);
                        diff.setDocValue(indexName + " (" + String.join(", ", docIndex.getColumns()) + ")");
                        diff.setDbValue("不存在");
                        diff.setDescription("数据库表缺少索引：" + indexName);
                        diff.setFixScript(generateCreateIndexScript(tableName, docIndex));
                        diffs.add(diff);
                    } else {
                        // 索引存在，比对索引列
                        IndexStructure dbIndex = dbIndexes.get(indexName);
                        
                        if (!docIndex.getColumns().equals(dbIndex.getColumns())) {
                            DiffInfo diff = new DiffInfo();
                            diff.setDiffType("INDEX_COLUMNS_MISMATCH");
                            diff.setTableName(tableName);
                            diff.setColumnName(indexName);
                            diff.setDocValue(String.join(", ", docIndex.getColumns()));
                            diff.setDbValue(String.join(", ", dbIndex.getColumns()));
                            diff.setDescription("索引列不匹配：" + indexName);
                            diff.setFixScript(generateRecreateIndexScript(tableName, docIndex));
                            diffs.add(diff);
                        }
                    }
                }
                
                // 检查数据库中的索引是否在文档中存在
                for (Map.Entry<String, IndexStructure> dbIndexEntry : dbIndexes.entrySet()) {
                    String indexName = dbIndexEntry.getKey();
                    
                    if (!docIndexes.containsKey(indexName)) {
                        // 数据库中的索引在文档中不存在
                        IndexStructure dbIndex = dbIndexEntry.getValue();
                        DiffInfo diff = new DiffInfo();
                        diff.setDiffType("INDEX_EXTRA");
                        diff.setTableName(tableName);
                        diff.setColumnName(indexName);
                        diff.setDocValue("不存在");
                        diff.setDbValue(indexName + " (" + String.join(", ", dbIndex.getColumns()) + ")");
                        diff.setDescription("文档中缺少索引：" + indexName);
                        diff.setFixScript(""); // 不自动生成删除索引的脚本，避免误操作
                        diffs.add(diff);
                    }
                }
            }
        }
    }

    // 生成创建表的 SQL 脚本
    private String generateCreateTableScript(TableStructure table) {
        StringBuilder script = new StringBuilder();
        script.append("CREATE TABLE `").append(table.getTableName()).append("` (\n");
        
        List<ColumnStructure> columns = table.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            ColumnStructure column = columns.get(i);
            script.append("  `").append(column.getColumnName()).append("` ").append(column.getColumnType());
            
            if (!column.isNullable()) {
                script.append(" NOT NULL");
            }
            
            if (!column.getDefaultValue().isEmpty()) {
                script.append(" DEFAULT '").append(column.getDefaultValue()).append("'");
            }
            
            if (!column.getColumnComment().isEmpty()) {
                script.append(" COMMENT '").append(column.getColumnComment()).append("'");
            }
            
            if (i < columns.size() - 1) {
                script.append(",");
            }
            script.append("\n");
        }
        
        // 添加主键约束
        // 这里简化处理，实际项目中需要根据文档中的主键定义生成
        
        script.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='").append(table.getTableComment()).append("';");
        
        return script.toString();
    }

    // 生成添加字段的 SQL 脚本
    private String generateAddColumnScript(String tableName, ColumnStructure column) {
        StringBuilder script = new StringBuilder();
        script.append("ALTER TABLE `").append(tableName).append("` ADD COLUMN `").append(column.getColumnName()).append("` ").append(column.getColumnType());
        
        if (!column.isNullable()) {
            script.append(" NOT NULL");
        } else {
            script.append(" DEFAULT NULL");
        }
        
        if (!column.getColumnComment().isEmpty()) {
            script.append(" COMMENT '").append(column.getColumnComment()).append("'");
        }
        
        script.append(";\n");
        
        return script.toString();
    }

    // 生成修改字段的 SQL 脚本
    private String generateModifyColumnScript(String tableName, ColumnStructure column) {
        StringBuilder script = new StringBuilder();
        script.append("ALTER TABLE `").append(tableName).append("` MODIFY COLUMN `").append(column.getColumnName()).append("` ").append(column.getColumnType());
        
        if (!column.isNullable()) {
            script.append(" NOT NULL");
        } else {
            script.append(" DEFAULT NULL");
        }
        
        if (!column.getColumnComment().isEmpty()) {
            script.append(" COMMENT '").append(column.getColumnComment()).append("'");
        }
        
        script.append(";\n");
        
        return script.toString();
    }

    // 生成修改字段注释的 SQL 脚本
    private String generateModifyColumnCommentScript(String tableName, String columnName, String comment) {
        StringBuilder script = new StringBuilder();
        script.append("ALTER TABLE `").append(tableName).append("` MODIFY COLUMN `").append(columnName).append("` COMMENT '").append(comment).append("';\n");
        
        return script.toString();
    }

    // 生成创建索引的 SQL 脚本
    private String generateCreateIndexScript(String tableName, IndexStructure index) {
        StringBuilder script = new StringBuilder();
        
        if (index.isUnique()) {
            script.append("CREATE UNIQUE INDEX `").append(index.getIndexName()).append("` ON `").append(tableName).append("` (");
        } else {
            script.append("CREATE INDEX `").append(index.getIndexName()).append("` ON `").append(tableName).append("` (");
        }
        
        List<String> columns = index.getColumns();
        for (int i = 0; i < columns.size(); i++) {
            script.append("`").append(columns.get(i)).append("`");
            if (i < columns.size() - 1) {
                script.append(", ");
            }
        }
        
        script.append(");\n");
        
        return script.toString();
    }

    // 生成重建索引的 SQL 脚本
    private String generateRecreateIndexScript(String tableName, IndexStructure index) {
        StringBuilder script = new StringBuilder();
        script.append("DROP INDEX `").append(index.getIndexName()).append("` ON `").append(tableName).append("`;\n");
        script.append(generateCreateIndexScript(tableName, index));
        return script.toString();
    }
}
