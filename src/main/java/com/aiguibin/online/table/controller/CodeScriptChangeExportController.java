package com.aiguibin.online.table.controller;

import com.aiguibin.online.table.entity.CodeScriptChangeRecord;
import com.aiguibin.online.table.service.CodeScriptChangeRecordService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/api/change-records")
public class CodeScriptChangeExportController {

    @Resource
    private CodeScriptChangeRecordService service;

    @RequestMapping("/export")
    public void export(@RequestParam(required = false, defaultValue = "已合版") String currentStatus,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime,
                       @RequestParam(required = false, defaultValue = "1000") int limit,
                       HttpServletResponse response) throws Exception {


        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("code_script_change_record");

        int r = 0;
        Row header = sheet.createRow(r++);

        String[] titles = {"序号", "当前状态", "发版日期", "缺陷编号", "组别", "开发负责人", "分支名称", "服务名称", "问题描述", "影响分析", "解决方案", "涉及外部系统", "跨服务", "代码清单", "备注", "版本号", "变更描述", "创建时间", "更新时间", "创建人", "更新人", "删除标志"};
        for (int i = 0; i < titles.length; i++) {
            header.createCell(i).setCellValue(titles[i]);
        }
        DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<CodeScriptChangeRecord> rows = service.listForExport(currentStatus, startTime, endTime, limit);
        for (CodeScriptChangeRecord cr : rows) {
            Row row = sheet.createRow(r++);

            row.createCell(0x00).setCellValue(cr.getId() == null ? "" : String.valueOf(cr.getId()));
            row.createCell(0x01).setCellValue(cr.getCurrentStatus() == null ? "" : cr.getCurrentStatus());
            row.createCell(0x02).setCellValue(cr.getReleaseDate() == null ? "" : cr.getReleaseDate().toString());
            row.createCell(0x03).setCellValue(cr.getDefectNumber() == null ? "" : cr.getDefectNumber());
            row.createCell(0x04).setCellValue(cr.getGroupName() == null ? "" : cr.getGroupName());
            row.createCell(0x05).setCellValue(cr.getDeveloper() == null ? "" : cr.getDeveloper());
            row.createCell(0x06).setCellValue(cr.getBranchName() == null ? "" : cr.getBranchName());
            row.createCell(0x07).setCellValue(cr.getServiceName() == null ? "" : cr.getServiceName());
            row.createCell(0x08).setCellValue(cr.getProblemDescription() == null ? "" : cr.getProblemDescription());
            row.createCell(0x09).setCellValue(cr.getImpactAnalysis() == null ? "" : cr.getImpactAnalysis());
            row.createCell(0x0a).setCellValue(cr.getSolution() == null ? "" : cr.getSolution());
            row.createCell(0x0b).setCellValue(cr.getInvolveExternalSystem() == null ? "" : String.valueOf(cr.getInvolveExternalSystem()));
            row.createCell(0x0c).setCellValue(cr.getCrossService() == null ? "" : String.valueOf(cr.getCrossService()));
            row.createCell(0x0d).setCellValue(cr.getCodeList() == null ? "" : cr.getCodeList());
            row.createCell(0x0e).setCellValue(cr.getRemark() == null ? "" : cr.getRemark());
            row.createCell(0x0f).setCellValue(cr.getVersion() == null ? "" : cr.getVersion());
            row.createCell(0x10).setCellValue(cr.getChangeDesc() == null ? "" : cr.getChangeDesc());
            row.createCell(0x11).setCellValue(cr.getCreateTime() == null ? "" : dt.format(cr.getCreateTime()));
            row.createCell(0x12).setCellValue(cr.getUpdateTime() == null ? "" : dt.format(cr.getUpdateTime()));
            row.createCell(0x13).setCellValue(cr.getCreateUser() == null ? "" : cr.getCreateUser());
            row.createCell(0x14).setCellValue(cr.getUpdateUser() == null ? "" : cr.getUpdateUser());
            row.createCell(0x15).setCellValue(cr.getIsDeleted() == null ? "" : String.valueOf(cr.getIsDeleted()));
        }

        for (int i = 0; i < titles.length; i++) {
            sheet.autoSizeColumn(i);
        }
        ServletOutputStream output = response.getOutputStream();
        String filename = "code_script_change_record_" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx";
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, "UTF-8"));
        workbook.write(output);
    }
}


