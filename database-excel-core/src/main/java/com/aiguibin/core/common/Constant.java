package com.aiguibin.core.common;

public class Constant {

    // 数据库表SVN目录
    public static final String DB_TABLE_LIST="D:\\DataBase\\TableList\\";
    // 程序执行输出的临时结果过程文件
    public static final String CHECK_LIST_OUT="D:\\DataBase\\CheckList\\";
    // 把各组SVN数据库文档跑出来一个平铺的目录
    public static final String DB_TARGET_LIST="D:\\DataBase\\CheckList\\数据库库表汇总\\";
    // 慢SQL收集目录
    public static final String SLOW_SQL_BASE="D:\\DataBase\\CheckList\\慢sql性能优化\\";
    // 第一步输出慢SQL（合并所有慢SQL到一个EXCEL文件一个SHEET页）
    public static final String SLOW_SQL_XLSX="slow_sql.xlsx";
    // 第二步输出慢SQL文件增加提取表名称
    public static final String SLOW_SQL_UPDATED="slow_sql_updated.xlsx";
    // 第三步输出根据数据库库表列表匹配提取各能力中心的慢SQL文件用于慢SQL分组下发任务
    public static final String SLOW_SQL_UPDATED_TASK="slow_sql_updated_task.xlsx";
    // 慢SQL前提根据数据库设计文档输出库表目录含能力中心
    public static final String TABLE_LIST_FOR_SLOW_SQL="table_list_for_slow_sql.xlsx";

    // 慢INTERFACE收集目录
    public static final String SLOW_INTERFACE_BASE="D:\\DataBase\\CheckList\\慢接口优化\\";
    // 输出慢接口文件
    public static final String SLOW_INTERFACE_XLSX="slow_interface.xlsx";


}
