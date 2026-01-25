package com.aiguibin.platform.arch.vo;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ValidationResult {
    private String validationId;     // 校验任务ID
    private String dbConfigId;       // 数据库配置ID
    private String dbName;           // 数据库名称
    private String documentName;     // 文档名称
    private LocalDateTime startTime; // 开始时间
    private LocalDateTime endTime;   // 结束时间
    private boolean success;         // 是否成功
    private String errorMessage;     // 错误信息
    private int totalTables;         // 总表数
    private int diffCount;           // 差异数
    private List<DiffInfo> diffs;     // 差异列表
    private String reportUrl;        // 报告URL
    private String fixScriptUrl;     // 修复脚本URL
}
