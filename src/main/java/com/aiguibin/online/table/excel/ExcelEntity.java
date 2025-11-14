package com.aiguibin.online.table.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ExcelEntity {

    @ExcelProperty("序号")
    @ColumnWidth(5)
    private Long id;
    @ExcelProperty("当前状态")
    @ColumnWidth(20)
    private String currentStatus;
    @ExcelProperty("发版日期")
    @ColumnWidth(20)
    private LocalDate releaseDate;
    @ExcelProperty("缺陷编号")
    @ColumnWidth(20)
    private String defectNumber;
    @ExcelProperty("组别")
    @ColumnWidth(20)
    private String groupName;
    @ExcelProperty("开发负责人")
    @ColumnWidth(20)
    private String developer;
    @ExcelProperty("分支名称")
    @ColumnWidth(20)
    private String branchName;
    @ExcelProperty("服务名称")
    @ColumnWidth(20)
    private String serviceName;
    @ExcelProperty("问题描述")
    @ColumnWidth(20)
    private String problemDescription;
    @ExcelProperty("影响分析")
    @ColumnWidth(20)
    private String impactAnalysis;
    @ExcelProperty("解决方案")
    @ColumnWidth(20)
    private String solution;
    @ExcelProperty("涉及外部系统")
    @ColumnWidth(20)
    private Integer involveExternalSystem;
    @ExcelProperty("跨服务")
    @ColumnWidth(20)
    private Integer crossService;
    @ExcelProperty("代码清单")
    @ColumnWidth(20)
    private String codeList;
    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String remark;
    @ExcelProperty("版本号")
    @ColumnWidth(20)
    private String version;
    @ExcelProperty("变更描述")
    @ColumnWidth(20)
    private String changeDesc;
    @ExcelProperty("创建时间")
    @ColumnWidth(20)
    private LocalDateTime createTime;
    @ExcelProperty("更新时间")
    @ColumnWidth(20)
    private LocalDateTime updateTime;
    @ExcelProperty("创建人")
    @ColumnWidth(20)
    private String createUser;
    @ExcelProperty("更新人")
    @ColumnWidth(20)
    private String updateUser;
    @ExcelProperty("删除标志")
    @ColumnWidth(20)
    private Integer isDeleted;
}
