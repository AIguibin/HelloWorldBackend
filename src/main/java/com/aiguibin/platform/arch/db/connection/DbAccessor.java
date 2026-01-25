package com.aiguibin.platform.arch.db.connection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbAccessor {
    private ConnectionPoolManager poolManager;

    // 构造函数
    public DbAccessor(ConnectionPoolManager poolManager) {
        this.poolManager = poolManager;
    }

    // 执行查询
    public List<Map<String, Object>> query(String configId, String sql, Object... params) {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            // 获取数据库连接
            conn = poolManager.getConnection(configId);
            // 创建 PreparedStatement
            ps = conn.prepareStatement(sql);
            // 设置参数
            setParameters(ps, params);
            // 执行查询
            rs = ps.executeQuery();
            // 处理结果集
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            closeResources(rs, ps, conn);
        }

        return result;
    }

    // 执行更新
    public int update(String configId, String sql, Object... params) {
        int rowsAffected = 0;
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            // 获取数据库连接
            conn = poolManager.getConnection(configId);
            // 创建 PreparedStatement
            ps = conn.prepareStatement(sql);
            // 设置参数
            setParameters(ps, params);
            // 执行更新
            rowsAffected = ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            closeResources(null, ps, conn);
        }

        return rowsAffected;
    }

    // 执行批量更新
    public int[] batchUpdate(String configId, String sql, List<Object[]> paramsList) {
        int[] rowsAffected = new int[0];
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            // 获取数据库连接
            conn = poolManager.getConnection(configId);
            // 创建 PreparedStatement
            ps = conn.prepareStatement(sql);
            // 批量设置参数
            for (Object[] params : paramsList) {
                setParameters(ps, params);
                ps.addBatch();
            }
            // 执行批量更新
            rowsAffected = ps.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            closeResources(null, ps, conn);
        }

        return rowsAffected;
    }

    // 设置 PreparedStatement 参数
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
        }
    }

    // 关闭资源
    private void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 执行存储过程
    public Map<String, Object> executeStoredProcedure(String configId, String procedureName, Map<String, Object> inParams) {
        Map<String, Object> result = new HashMap<>();
        Connection conn = null;
        CallableStatement cs = null;

        try {
            // 获取数据库连接
            conn = poolManager.getConnection(configId);
            // 创建 CallableStatement
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("{call ").append(procedureName).append("(");

            // 构建参数占位符
            if (inParams != null && !inParams.isEmpty()) {
                for (int i = 0; i < inParams.size(); i++) {
                    if (i > 0) {
                        sqlBuilder.append(", ");
                    }
                    sqlBuilder.append("?");
                }
            }
            sqlBuilder.append(")}");

            cs = conn.prepareCall(sqlBuilder.toString());

            // 设置输入参数
            if (inParams != null && !inParams.isEmpty()) {
                int index = 1;
                for (Map.Entry<String, Object> entry : inParams.entrySet()) {
                    cs.setObject(index++, entry.getValue());
                }
            }

            // 执行存储过程
            boolean hasResultSet = cs.execute();

            // 处理结果集
            if (hasResultSet) {
                ResultSet rs = cs.getResultSet();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                List<Map<String, Object>> resultSet = new ArrayList<>();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    resultSet.add(row);
                }
                result.put("resultSet", resultSet);
                rs.close();
            }

            // 处理输出参数
            // 这里需要根据具体存储过程的输出参数进行处理

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (cs != null) {
                    cs.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
