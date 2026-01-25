package com.aiguibin.platform.arch.excel;

import com.aiguibin.platform.arch.db.connection.ConnectionPoolManager;
import com.aiguibin.platform.arch.db.validation.MetadataFetcher;
import com.aiguibin.platform.arch.service.DbConfigService;
import com.aiguibin.platform.arch.vo.DbConfig;
import com.aiguibin.platform.arch.vo.TableStructure;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class DatabaseDocGenerator {
    @Autowired
    private DbConfigService dbConfigService;
    private ConnectionPoolManager poolManager = new ConnectionPoolManager();
    private MetadataFetcher metadataFetcher = new MetadataFetcher(poolManager);
    private ExcelDocBuilder excelDocBuilder = new ExcelDocBuilder();

    public String generateDoc(String dbConfigId, List<String> tableNames) throws IOException {
        DbConfig dbConfig = dbConfigService.getDbConfig(dbConfigId);
        if (dbConfig == null) {
            throw new IllegalArgumentException("数据库配置不存在");
        }

        String filePath = generateOutputPath(dbConfig.getDatabase());
        Workbook workbook = excelDocBuilder.createWorkbook(filePath);

        try {
            List<TableInfo> tableInfos = new java.util.ArrayList<>();
            List<TableStructure> tableStructures = new java.util.ArrayList<>();

            if (tableNames == null || tableNames.isEmpty()) {
                tableNames = metadataFetcher.getAllTables(dbConfigId, dbConfig.getDatabase());
            }

            int sheetIndex = 3;
            for (String tableName : tableNames) {
                TableStructure tableStructure = metadataFetcher.getTableStructure(dbConfigId, dbConfig.getDatabase(), tableName);
                tableStructures.add(tableStructure);

                TableInfo tableInfo = new TableInfo();
                tableInfo.setTableName(tableName);
                tableInfo.setTableComment(tableStructure.getTableComment());
                tableInfo.setSheetIndex(sheetIndex);
                tableInfos.add(tableInfo);

                sheetIndex++;
            }

            excelDocBuilder.addChangeRecordSheet(workbook);
            excelDocBuilder.addTableOfContentsSheet(workbook, tableInfos);
            excelDocBuilder.addTableIndexSheet(workbook, tableInfos);

            for (TableStructure tableStructure : tableStructures) {
                excelDocBuilder.addTableSheet(workbook, tableStructure);
            }

            excelDocBuilder.saveWorkbook(workbook);
            return filePath;
        } finally {
            if (workbook != null) {
                workbook.close();
            }
        }
    }

    public void generateDocForAllDatabases() throws IOException {
        List<DbConfig> dbConfigs = dbConfigService.getDbConfigs();
        for (DbConfig dbConfig : dbConfigs) {
            generateDoc(dbConfig.getId(), null);
        }
    }

    private String generateOutputPath(String database) {
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String outputDir = System.getProperty("user.dir") + File.separator + "database_docs";
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return outputDir + File.separator + database + "_db_doc_" + timestamp + ".xlsx";
    }
}