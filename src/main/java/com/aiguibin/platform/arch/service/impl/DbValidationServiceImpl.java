package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.db.connection.ConnectionPoolManager;
import com.aiguibin.platform.arch.db.validation.DocParser;
import com.aiguibin.platform.arch.db.validation.MetadataFetcher;
import com.aiguibin.platform.arch.db.validation.StructureComparator;
import com.aiguibin.platform.arch.db.validation.ReportGenerator;
import com.aiguibin.platform.arch.service.DbValidationService;
import com.aiguibin.platform.arch.service.DbConfigService;
import com.aiguibin.platform.arch.dto.DbConfig;
import com.aiguibin.platform.arch.dto.DiffInfo;
import com.aiguibin.platform.arch.dto.TableStructure;
import com.aiguibin.platform.arch.dto.ValidationRequest;
import com.aiguibin.platform.arch.dto.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DbValidationServiceImpl implements DbValidationService {
    @Autowired
    private DbConfigService dbConfigService;
    private Map<String, ValidationResult> validationResults = new HashMap<>();
    private ConnectionPoolManager poolManager = new ConnectionPoolManager();

    @Override
    public String executeValidation(ValidationRequest request) {
        String validationId = UUID.randomUUID().toString();
        ValidationResult result = new ValidationResult();
        result.setValidationId(validationId);
        result.setDbConfigId(request.getDbConfigId());
        result.setStartTime(LocalDateTime.now());

        try {
            // 获取数据库连接配置
            DbConfig dbConfig = dbConfigService.getDbConfig(request.getDbConfigId());
            if (dbConfig == null) {
                throw new IllegalArgumentException("数据库连接配置不存在");
            }

            // 初始化连接池
            poolManager.createDataSource(dbConfig);

            // 解析表结构文档
            DocParser docParser = new DocParser();
            File docFile = new File(request.getDocumentPath());
            Map<String, TableStructure> docStructures = docParser.parseExcel(docFile);

            // 获取数据库元数据
            MetadataFetcher metadataFetcher = new MetadataFetcher(poolManager);
            Map<String, TableStructure> dbStructures = metadataFetcher.getTableStructures(request.getDbConfigId(), dbConfig.getDatabase());

            // 执行结构比对
            StructureComparator comparator = new StructureComparator();
            List<DiffInfo> diffs = comparator.compare(docStructures, dbStructures);

            // 生成差异报告
            ReportGenerator reportGenerator = new ReportGenerator();
            byte[] reportBytes = reportGenerator.generateReport(result);

            // 设置校验结果
            result.setEndTime(LocalDateTime.now());
            result.setSuccess(true);
            result.setDbName(dbConfig.getDatabase());
            result.setDocumentName(docFile.getName());
            result.setTotalTables(docStructures.size());
            result.setDiffCount(diffs.size());
            result.setDiffs(diffs);
            result.setReportUrl("/api/db/validation/" + validationId + "/report");
            result.setFixScriptUrl("/api/db/validation/" + validationId + "/fix-script");

        } catch (Exception e) {
            result.setEndTime(LocalDateTime.now());
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            e.printStackTrace();
        } finally {
            // 释放连接池
            poolManager.releaseDataSource(request.getDbConfigId());
        }

        validationResults.put(validationId, result);
        return validationId;
    }

    @Override
    public ValidationResult getValidationTask(String id) {
        return validationResults.get(id);
    }

    @Override
    public List<ValidationResult> getValidationTasks() {
        return new ArrayList<>(validationResults.values());
    }

    @Override
    public byte[] getReport(String id) {
        ValidationResult result = validationResults.get(id);
        if (result != null) {
            ReportGenerator reportGenerator = new ReportGenerator();
            try {
                return reportGenerator.generateReport(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new byte[0];
    }

    @Override
    public byte[] getFixScript(String id) {
        ValidationResult result = validationResults.get(id);
        if (result != null) {
            // 生成修复脚本
            StringBuilder script = new StringBuilder();
            for (DiffInfo diff : result.getDiffs()) {
                script.append(diff.getFixScript()).append("\n");
            }
            return script.toString().getBytes();
        }
        return new byte[0];
    }

    @Override
    public void deleteValidationTask(String id) {
        validationResults.remove(id);
    }
}
