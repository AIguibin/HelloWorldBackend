# 生成数据库表设计文档Excel功能实现

## 一、需求分析

### 1. 功能需求
- 根据指定数据库生成数据库表设计文档Excel文件
- 一个数据库对应一个Excel文件
- 每个Excel文件包含多个sheet页：
  - 第一个sheet页：变更记录
  - 第二个sheet页：目录（包含该数据库所有表，并增加超链接到对应表的sheet页）
  - 第三个sheet页：表索引
  - 往后：一个表一个sheet页
- 支持多种数据库类型（MySQL、Oracle、PostgreSQL等）
- 自动获取表结构信息，包括字段、索引等

### 2. 非功能需求
- 性能：处理大型数据库时保持响应速度
- 可靠性：确保生成的文档格式正确，内容完整
- 可扩展性：支持未来添加更多数据库类型和文档格式
- 兼容性：生成的Excel文件兼容主流Office软件

## 二、详细设计

### 1. 系统架构
- **模块划分**：
  - 数据库连接管理模块：负责获取数据库连接
  - 元数据获取模块：负责获取数据库表结构信息
  - Excel生成模块：负责生成Excel文档
  - 主控制模块：协调各模块工作

### 2. 核心类设计

#### 2.1 DatabaseDocGenerator
- **职责**：主控制器，协调各模块生成数据库文档
- **方法**：
  - `generateDoc(DbConfig dbConfig, List<String> tableNames)`：生成指定数据库的表设计文档
  - `generateDocForAllDatabases(List<DbConfig> dbConfigs)`：为所有数据库生成表设计文档

#### 2.2 MetadataCollector
- **职责**：获取数据库元数据
- **方法**：
  - `getAllTables(String configId, String database)`：获取数据库所有表
  - `getTableStructure(String configId, String database, String tableName)`：获取表结构
  - `getTableIndexes(String configId, String database, String tableName)`：获取表索引

#### 2.3 ExcelDocBuilder
- **职责**：构建Excel文档
- **方法**：
  - `createWorkbook(String filePath)`：创建工作簿
  - `addChangeRecordSheet(Workbook workbook)`：添加变更记录sheet
  - `addTableOfContentsSheet(Workbook workbook, List<TableInfo> tables)`：添加目录sheet
  - `addTableIndexSheet(Workbook workbook, List<TableInfo> tables)`：添加表索引sheet
  - `addTableSheet(Workbook workbook, TableStructure tableStructure)`：添加表结构sheet
  - `saveWorkbook(Workbook workbook)`：保存工作簿

#### 2.4 TableInfo
- **职责**：存储表基本信息
- **属性**：
  - `tableName`：表名
  - `tableComment`：表注释
  - `sheetIndex`：对应sheet页索引

### 3. 数据结构设计

#### 3.1 ChangeRecord
- **属性**：
  - `version`：版本号
  - `changeDate`：变更日期
  - `changer`：变更人
  - `changeContent`：变更内容

#### 3.2 TableStructure（复用现有类）
- **属性**：
  - `tableName`：表名
  - `tableComment`：表注释
  - `columns`：字段列表
  - `indexes`：索引列表

### 4. 伪代码实现

#### 4.1 主流程
```java
public void generateDatabaseDoc(DbConfig dbConfig, List<String> tableNames) {
    // 1. 创建输出文件路径
    String filePath = generateOutputPath(dbConfig.getDatabase());
    
    // 2. 创建工作簿
    Workbook workbook = excelDocBuilder.createWorkbook(filePath);
    
    try {
        // 3. 获取表结构信息
        List<TableInfo> tableInfos = new ArrayList<>();
        List<TableStructure> tableStructures = new ArrayList<>();
        
        if (tableNames == null || tableNames.isEmpty()) {
            // 获取所有表
            tableNames = metadataCollector.getAllTables(dbConfig.getId(), dbConfig.getDatabase());
        }
        
        // 4. 为每个表获取结构信息
        int sheetIndex = 3; // 从第4个sheet开始（0-based）
        for (String tableName : tableNames) {
            TableStructure tableStructure = metadataCollector.getTableStructure(
                dbConfig.getId(), dbConfig.getDatabase(), tableName);
            tableStructures.add(tableStructure);
            
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName(tableName);
            tableInfo.setTableComment(tableStructure.getTableComment());
            tableInfo.setSheetIndex(sheetIndex);
            tableInfos.add(tableInfo);
            
            sheetIndex++;
        }
        
        // 5. 添加变更记录sheet
        excelDocBuilder.addChangeRecordSheet(workbook);
        
        // 6. 添加目录sheet
        excelDocBuilder.addTableOfContentsSheet(workbook, tableInfos);
        
        // 7. 添加表索引sheet
        excelDocBuilder.addTableIndexSheet(workbook, tableInfos);
        
        // 8. 为每个表添加sheet
        for (int i = 0; i < tableStructures.size(); i++) {
            TableStructure tableStructure = tableStructures.get(i);
            excelDocBuilder.addTableSheet(workbook, tableStructure);
        }
        
        // 9. 保存工作簿
        excelDocBuilder.saveWorkbook(workbook);
        
    } finally {
        // 10. 关闭资源
        workbook.close();
    }
}
```

#### 4.2 添加目录sheet
```java
public void addTableOfContentsSheet(Workbook workbook, List<TableInfo> tableInfos) {
    Sheet sheet = workbook.createSheet("目录");
    
    // 创建标题行
    Row titleRow = sheet.createRow(0);
    titleRow.createCell(0).setCellValue("序号");
    titleRow.createCell(1).setCellValue("表名");
    titleRow.createCell(2).setCellValue("表注释");
    titleRow.createCell(3).setCellValue("链接");
    
    // 填充数据
    for (int i = 0; i < tableInfos.size(); i++) {
        TableInfo tableInfo = tableInfos.get(i);
        Row row = sheet.createRow(i + 1);
        
        row.createCell(0).setCellValue(i + 1);
        row.createCell(1).setCellValue(tableInfo.getTableName());
        row.createCell(2).setCellValue(tableInfo.getTableComment());
        
        // 创建超链接
        Cell linkCell = row.createCell(3);
        linkCell.setCellValue("查看详情");
        
        CreationHelper creationHelper = workbook.getCreationHelper();
        Hyperlink hyperlink = creationHelper.createHyperlink(HyperlinkType.DOCUMENT);
        hyperlink.setAddress("#'" + tableInfo.getTableName() + "'!A1");
        linkCell.setHyperlink(hyperlink);
    }
}
```

#### 4.3 获取表结构信息
```java
public TableStructure getTableStructure(String configId, String database, String tableName) {
    TableStructure tableStructure = new TableStructure();
    tableStructure.setTableName(tableName);
    
    Connection conn = null;
    try {
        // 获取数据库连接
        conn = poolManager.getConnection(configId);
        
        // 获取表注释
        String tableComment = getTableComment(conn, database, tableName);
        tableStructure.setTableComment(tableComment);
        
        // 获取字段信息
        List<ColumnStructure> columns = getColumns(conn, database, tableName);
        tableStructure.setColumns(columns);
        
        // 获取索引信息
        List<IndexStructure> indexes = getIndexes(conn, database, tableName);
        tableStructure.setIndexes(indexes);
        
    } catch (SQLException e) {
        e.printStackTrace();
    } finally {
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
```

## 三、执行计划

### 1. 准备阶段
- **步骤1**：分析现有代码结构，确定集成点
- **步骤2**：创建必要的目录结构和文件
- **步骤3**：定义核心数据结构和VO类

### 2. 核心功能实现
- **步骤4**：实现MetadataCollector类，用于获取数据库元数据
- **步骤5**：实现ExcelDocBuilder类，用于构建Excel文档
- **步骤6**：实现DatabaseDocGenerator类，协调各模块工作
- **步骤7**：实现超链接功能，在目录中添加到对应表sheet的链接
- **步骤8**：实现表索引sheet的生成

### 3. 测试与优化
- **步骤9**：编写单元测试，验证各模块功能
- **步骤10**：测试不同数据库类型的兼容性
- **步骤11**：测试大型数据库的处理性能
- **步骤12**：优化代码，提高性能和可靠性

### 4. 集成与部署
- **步骤13**：将功能集成到现有系统中
- **步骤14**：编写使用文档和示例
- **步骤15**：部署功能到开发环境进行验证

## 四、技术要点

1. **数据库元数据获取**：使用JDBC DatabaseMetaData接口获取表结构信息
2. **Excel文档生成**：使用EasyExcel库生成Excel文件，支持多个sheet页
3. **超链接实现**：使用POI的Hyperlink功能实现目录到表sheet的链接
4. **多数据库支持**：通过策略模式适配不同数据库类型的元数据获取方式
5. **性能优化**：
   - 批量处理表结构信息
   - 使用连接池管理数据库连接
   - 合理使用缓存减少数据库访问

## 五、预期成果

- 一个功能完整的数据库表设计文档生成工具
- 支持多种数据库类型
- 生成的文档格式规范，内容完整
- 集成到现有项目中，可直接使用
- 提供清晰的使用文档和示例