package com.aiguibin.platform.arch.db.validation;

import com.aiguibin.platform.arch.db.connection.ConnectionPoolManager;
import com.aiguibin.platform.arch.vo.ColumnStructure;
import com.aiguibin.platform.arch.vo.IndexStructure;
import com.aiguibin.platform.arch.vo.TableStructure;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetadataFetcher {
    private ConnectionPoolManager poolManager;

    // 构造函数
    public MetadataFetcher(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    // 获取数据库所有表结构
    public Map<String, TableStructure> getTableStructures(String configId, String database) {
        Map<String, TableStructure> tableStructures = new HashMap<>();
        Connection conn = null;

        try {
            // 获取数据库连接
            conn = poolManager.getConnection(configId);

            // 获取数据库元数据
            DatabaseMetaData metaData = conn.getMetaData();

            // 获取所有表名
            ResultSet tablesResultSet = metaData.getTables(database, null, null, new String[]{"TABLE"});

            // 遍历每个表，获取表结构信息
            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                String tableComment = tablesResultSet.getString("REMARKS");

                // 构建 TableStructure 对象
                TableStructure tableStructure = getTableStructure(conn, database, tableName);
                tableStructure.setTableComment(tableComment != null ? tableComment : "");

                tableStructures.put(tableName, tableStructure);
            }

            tablesResultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return tableStructures;
    }

    // 获取单个表的结构
    private TableStructure getTableStructure(Connection conn, String database, String tableName) {
        TableStructure tableStructure = new TableStructure();
        tableStructure.setTableName(tableName);

        // 获取字段信息
        List<ColumnStructure> columns = getColumns(conn, database, tableName);
        tableStructure.setColumns(columns);

        // 获取索引信息
        List<IndexStructure> indexes = getIndexes(conn, database, tableName);
        tableStructure.setIndexes(indexes);

        return tableStructure;
    }

    // 获取字段信息
    private List<ColumnStructure> getColumns(Connection conn, String database, String tableName) {
        List<ColumnStructure> columns = new ArrayList<>();
        DatabaseMetaData metaData = null;

        try {
            metaData = conn.getMetaData();

            // 使用 DatabaseMetaData 获取字段信息
            ResultSet columnsResultSet = metaData.getColumns(database, null, tableName, null);

            while (columnsResultSet.next()) {
                ColumnStructure column = new ColumnStructure();

                // 字段名
                String columnName = columnsResultSet.getString("COLUMN_NAME");
                column.setColumnName(columnName);

                // 字段类型
                String columnType = columnsResultSet.getString("TYPE_NAME");
                int columnSize = columnsResultSet.getInt("COLUMN_SIZE");
                String fullType = columnType;
                if (columnSize > 0) {
                    fullType += "(" + columnSize + ")";
                }
                column.setColumnType(fullType);
                column.setColumnLength(columnSize);

                // 字段注释
                String columnComment = columnsResultSet.getString("REMARKS");
                column.setColumnComment(columnComment != null ? columnComment : "");

                // 是否可为空
                int nullable = columnsResultSet.getInt("NULLABLE");
                column.setNullable(nullable == DatabaseMetaData.columnNullable);

                // 默认值
                String defaultValue = columnsResultSet.getString("COLUMN_DEF");
                column.setDefaultValue(defaultValue != null ? defaultValue : "");

                columns.add(column);
            }

            columnsResultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return columns;
    }

    // 获取索引信息
    private List<IndexStructure> getIndexes(Connection conn, String database, String tableName) {
        List<IndexStructure> indexes = new ArrayList<>();
        Map<String, IndexStructure> indexMap = new HashMap<>();
        DatabaseMetaData metaData = null;

        try {
            metaData = conn.getMetaData();

            // 使用 DatabaseMetaData 获取索引信息
            ResultSet indexesResultSet = metaData.getIndexInfo(database, null, tableName, false, false);

            while (indexesResultSet.next()) {
                String indexName = indexesResultSet.getString("INDEX_NAME");
                if (indexName == null) {
                    continue; // 跳过主键索引的额外条目
                }

                String columnName = indexesResultSet.getString("COLUMN_NAME");
                boolean nonUnique = indexesResultSet.getBoolean("NON_UNIQUE");

                // 检查是否已存在该索引
                IndexStructure index = indexMap.get(indexName);
                if (index == null) {
                    index = new IndexStructure();
                    index.setIndexName(indexName);
                    index.setUnique(!nonUnique);
                    index.setColumns(new ArrayList<>());
                    indexMap.put(indexName, index);
                    indexes.add(index);
                }

                // 添加索引列
                index.getColumns().add(columnName);
            }

            indexesResultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return indexes;
    }

    // 获取数据库所有表名
    public List<String> getAllTables(String configId, String database) {
        List<String> tables = new ArrayList<>();
        Connection conn = null;

        try {
            // 获取数据库连接
            conn = poolManager.getConnection(configId);

            // 获取数据库元数据
            DatabaseMetaData metaData = conn.getMetaData();

            // 获取所有表名
            ResultSet tablesResultSet = metaData.getTables(database, null, null, new String[]{"TABLE"});

            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString("TABLE_NAME");
                tables.add(tableName);
            }

            tablesResultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return tables;
    }

    // 获取单个表的结构（公共方法）
    public TableStructure getTableStructure(String configId, String database, String tableName) {
        TableStructure tableStructure = new TableStructure();
        Connection conn = null;

        try {
            // 获取数据库连接
            conn = poolManager.getConnection(configId);

            // 获取表注释
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tablesResultSet = metaData.getTables(database, null, tableName, new String[]{"TABLE"});
            if (tablesResultSet.next()) {
                String tableComment = tablesResultSet.getString("REMARKS");
                tableStructure.setTableComment(tableComment != null ? tableComment : "");
            }
            tablesResultSet.close();

            // 获取字段信息
            List<ColumnStructure> columns = getColumns(conn, database, tableName);
            tableStructure.setColumns(columns);

            // 获取索引信息
            List<IndexStructure> indexes = getIndexes(conn, database, tableName);
            tableStructure.setIndexes(indexes);

            tableStructure.setTableName(tableName);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return tableStructure;
    }

    // 获取数据库类型
    public String getDatabaseType(String configId) {
        String databaseType = "unknown";
        Connection conn = null;

        try {
            // 获取数据库连接
            conn = poolManager.getConnection(configId);

            // 获取数据库元数据
            DatabaseMetaData metaData = conn.getMetaData();

            // 获取数据库产品名称
            String productName = metaData.getDatabaseProductName();

            // 根据产品名称判断数据库类型
            if (productName.contains("MySQL")) {
                databaseType = "mysql";
            } else if (productName.contains("Oracle")) {
                databaseType = "oracle";
            } else if (productName.contains("PostgreSQL")) {
                databaseType = "postgresql";
            } else if (productName.contains("SQL Server")) {
                databaseType = "sqlserver";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return databaseType;
    }
}
