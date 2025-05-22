package com.aiguibin.core.converter;





import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlTableNameConverter {

    public static String getTableName(String sql) {
        String cleanSql=removeComments(sql.toUpperCase()).trim();
        String tableName = getTableNameByRegex(cleanSql);
        if (null == tableName || tableName.length() == 0 || "" == tableName) {
            tableName = getMainTableName(cleanSql);
        }
        return tableName;
    }


    public static String getTableNameByRegex(String sql) {
        if (sql.startsWith("UPDATE")) {
            return processUpdate(sql);
        } else if (sql.startsWith("DELETE") || sql.startsWith("SELECT")) {
            return processDeleteOrSelect(sql);
        } else if (sql.startsWith("ALTER")) {
            return processAlter(sql);
        } else if (sql.startsWith("INSERT")) {  // 新增INSERT分支
            return processInsert(sql);
        } else {
            System.out.println(sql);
            throw new IllegalArgumentException("Unsupported SQL type");
        }
    }

    public static String removeComments(String sql) {
        // 去除外层括号（最多处理3层）

            if (sql.startsWith("(") ) {
                sql = sql.substring(1, sql.length()).trim();
                sql.indexOf(")");

        }
        // 匹配注释时保留关键结构（处理括号后紧跟注释的情况）
        String noMultiLine = sql
                // 处理 /* */ 注释（保留注释前后非括号区域）
                .replaceAll("(?s)(\\(\\s*)/\\*.*?\\*/(\\s*)", "$1$2")
                // 处理其他位置的注释
                .replaceAll("(?s)/\\*.*?\\*/", " ")
                // 处理单行注释
                .replaceAll("--.*", " ");

        // 统一处理空白字符
        return noMultiLine
                .replaceAll("<br>", " ")
                .replaceAll("\\n", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static String processUpdate(String sql) {
        Matcher matcher = Pattern.compile("^UPDATE\\s+([^\\s]+)").matcher(sql);
        if (matcher.find()) {
            return sanitizeTableName(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid UPDATE statement");
    }

    public static String processDeleteOrSelect(String sql) {
        int fromIndex = sql.indexOf("FROM");
        if (fromIndex == -1) {
            throw new IllegalArgumentException("No FROM clause");
        }
        String fromPart = sql.substring(fromIndex + 4).trim();
        return processFromPart(fromPart);
    }

    public static String processAlter(String sql) {
        Matcher matcher = Pattern.compile("^ALTER\\s+TABLE\\s+([^\\s;]+)").matcher(sql);
        if (matcher.find()) {
            return sanitizeTableName(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid ALTER TABLE statement");
    }


    // 新增INSERT语句处理方法
    public static String processInsert(String sql) {
        // 匹配 INSERT INTO table_name 的模式
        Matcher matcher = Pattern.compile("^INSERT\\s+INTO\\s+([^\\s;(]+)").matcher(sql);
        if (matcher.find()) {
            return sanitizeTableName(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid INSERT statement");
    }

    public static String processFromPart(String fromPart) {
        if (fromPart.startsWith("(")) {
            int end = findMatchingParenthesis(fromPart, 0);
            if (end == -1) {
                throw new IllegalArgumentException("Unclosed parenthesis");
            }
            String subQuery = fromPart.substring(1, end).trim();
            return getTableName(subQuery);
        } else {
            Matcher matcher = Pattern.compile("^([^\\s,]+)").matcher(fromPart);
            if (matcher.find()) {
                return sanitizeTableName(matcher.group(1));
            }
            throw new IllegalArgumentException("No table found");
        }
    }

    public static int findMatchingParenthesis(String str, int start) {
        int count = 1;
        for (int i = start + 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '(') count++;
            else if (c == ')') {
                count--;
                if (count == 0) return i;
            }
        }
        return -1;
    }

    public static String sanitizeTableName(String tableName) {
        return tableName.replaceAll("[\"`]", "");
    }


    /**
     * 根据SQL语句提取主要的第一个表名
     *
     * @param sql 输入的SQL语句
     * @return 提取的表名，若未找到则 throw new IllegalArgumentException("Unsupported SQL type");
     */
    public static String getMainTableName(String sql) {
        if (sql == null || sql.trim().isEmpty()) {
            return null;
        }

        String upperSql = sql.toUpperCase();
        if (upperSql.startsWith("SELECT ") || upperSql.startsWith("DELETE ")) {
            return extractFromSelectDelete(upperSql);
        } else if (upperSql.startsWith("UPDATE ")) {
            return extractFromUpdate(upperSql);
        } else if (upperSql.startsWith("INSERT INTO ")) {
            return extractFromInsert(upperSql);
        } else if (upperSql.startsWith("ALTER TABLE ")) {
            return extractFromAlter(upperSql);
        } else {
            throw new IllegalArgumentException("Unsupported SQL type");
        }
    }

    /**
     * 处理 SELECT 和 DELETE 语句的表名提取
     */
    private static String extractFromSelectDelete(String sql) {
        int fromIndex = sql.indexOf("FROM");
        if (fromIndex == -1) {
            return null;
        }

        String fromClause = sql.substring(fromIndex + 4).trim();
        return extractTableName(fromClause);
    }

    /**
     * 处理 UPDATE 语句的表名提取
     */
    private static String extractFromUpdate(String sql) {
        int updateIndex = sql.indexOf("UPDATE");
        if (updateIndex == -1) {
            return null;
        }

        String afterUpdate = sql.substring(updateIndex + 6).trim();
        int endPos = 0;

        while (endPos < afterUpdate.length()) {
            char c = afterUpdate.charAt(endPos);
            if (Character.isWhitespace(c)) {
                break;
            }
            if (endPos + 3 < afterUpdate.length() && "SET".equals(afterUpdate.substring(endPos, endPos + 3))) {
                break;
            }
            endPos++;
        }

        return afterUpdate.substring(0, endPos).trim();
    }

    /**
     * 处理 INSERT INTO 语句的表名提取
     */
    private static String extractFromInsert(String sql) {
        int intoIndex = sql.indexOf("INTO");
        if (intoIndex == -1) {
            return null;
        }

        String afterInto = sql.substring(intoIndex + 4).trim();
        if (afterInto.startsWith("(")) {
            int end = findClosingParenthesis(afterInto);
            if (end != -1) {
                afterInto = afterInto.substring(end + 1).trim();
            }
        }

        return extractFirstToken(afterInto);
    }

    /**
     * 处理 ALTER TABLE 语句的表名提取
     */
    private static String extractFromAlter(String sql) {
        int tableIndex = sql.indexOf("TABLE");
        if (tableIndex == -1) {
            return null;
        }

        String afterTable = sql.substring(tableIndex + 5).trim();
        return extractFirstToken(afterTable);
    }

    /**
     * 递归提取表名，处理子查询
     */
    private static String extractTableName(String fromClause) {
        fromClause = fromClause.trim();
        if (fromClause.isEmpty()) {
            return null;
        }

        // 如果是子查询
        if (fromClause.startsWith("(")) {
            int depth = 1;
            int end = 1;
            while (end < fromClause.length() && depth > 0) {
                char c = fromClause.charAt(end);
                if (c == '(') {
                    depth++;
                } else if (c == ')') {
                    depth--;
                }
                end++;
            }

            if (depth == 0) {
                String subQuery = fromClause.substring(1, end - 1).trim();
                return extractTableName(subQuery);
            }
        }

        // 跳过空格
        int start = 0;
        while (start < fromClause.length() && Character.isWhitespace(fromClause.charAt(start))) {
            start++;
        }
        if (start >= fromClause.length()) {
            return null;
        }

        // 提取表名
        int endPos = start;
        while (endPos < fromClause.length()) {
            char c = fromClause.charAt(endPos);
            if (Character.isWhitespace(c) || c == ',') {
                break;
            }
            if (endPos + 3 < fromClause.length() && "JOIN".equals(fromClause.substring(endPos, endPos + 4))) {
                break;
            }
            if (endPos + 2 < fromClause.length() && "AS".equals(fromClause.substring(endPos, endPos + 2))) {
                break;
            }
            endPos++;
        }

        return fromClause.substring(start, endPos).trim();
    }

    /**
     * 找到第一个右括号位置
     */
    private static int findClosingParenthesis(String str) {
        int depth = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
                if (depth == 0) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 提取第一个 token，直到遇到空格或关键字
     */
    private static String extractFirstToken(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        str = str.trim();
        int end = 0;
        while (end < str.length() && !isDelimiter(str.charAt(end), end, str)) {
            end++;
        }
        return str.substring(0, end).trim();
    }

    /**
     * 判断是否是分隔符（空格或关键字）
     */
    private static boolean isDelimiter(char c, int index, String str) {
        if (Character.isWhitespace(c)) {
            return true;
        }

        String key = "JOIN WHERE ON SET FROM";
        for (int i = 0; i < key.length(); i++) {
            if (index + i >= str.length()) break;
            if (str.charAt(index + i) != key.charAt(i)) break;
            if (i == key.length() - 1) {
                return true;
            }
        }
        return false;
    }
}
