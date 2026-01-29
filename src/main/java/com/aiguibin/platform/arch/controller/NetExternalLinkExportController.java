package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.NetExternalLink;
import com.aiguibin.platform.arch.service.NetExternalLinkService;
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

/**
 * 信贷系统外部链接网络管理清单导出控制器
 */
@Controller
@RequestMapping("/api/net-links")
public class NetExternalLinkExportController {
    
    @Resource
    private NetExternalLinkService netExternalLinkService;
    
    /**
     * 导出Excel
     * 文件名格式：信贷系统外部链接网络管理清单_{筛选条件}_{日期}.xlsx
     */
    @RequestMapping("/export")
    public void export(@RequestParam(required = false) String sourceEnv,
                       @RequestParam(required = false) String targetEnv,
                       @RequestParam(required = false) String status,
                       @RequestParam(required = false) String protocol,
                       @RequestParam(required = false) String keyword,
                       HttpServletResponse response) throws Exception {
        
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ServletOutputStream output = response.getOutputStream()) {
            
            Sheet sheet = workbook.createSheet("外部链接网络管理清单");
            
            // 创建表头
            int rowIndex = 0;
            Row header = sheet.createRow(rowIndex++);
            
            String[] titles = {
                "链路编号", "链路名称", "源系统", "源环境", "源IP地址",
                "目标系统", "目标环境", "目标主机", "目标端口", "连接协议",
                "认证方式", "使用场景", "链路描述", "负责人", "链路状态",
                "监控等级", "创建时间", "更新时间", "创建人", "更新人"
            };
            
            for (int i = 0; i < titles.length; i++) {
                header.createCell(i).setCellValue(titles[i]);
            }
            
            // 查询数据
            List<NetExternalLink> links = netExternalLinkService.listForExport(
                sourceEnv, targetEnv, status, protocol, keyword);
            
            // 填充数据
            DateTimeFormatter dt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (NetExternalLink link : links) {
                Row row = sheet.createRow(rowIndex++);
                
                row.createCell(0).setCellValue(link.getLinkCode() == null ? "" : link.getLinkCode());
                row.createCell(1).setCellValue(link.getLinkName() == null ? "" : link.getLinkName());
                row.createCell(2).setCellValue(link.getSourceSystem() == null ? "" : link.getSourceSystem());
                row.createCell(3).setCellValue(link.getSourceEnv() == null ? "" : link.getSourceEnv());
                row.createCell(4).setCellValue(link.getSourceIp() == null ? "" : link.getSourceIp());
                row.createCell(5).setCellValue(link.getTargetSystem() == null ? "" : link.getTargetSystem());
                row.createCell(6).setCellValue(link.getTargetEnv() == null ? "" : link.getTargetEnv());
                row.createCell(7).setCellValue(link.getTargetHost() == null ? "" : link.getTargetHost());
                row.createCell(8).setCellValue(link.getTargetPort() == null ? "" : String.valueOf(link.getTargetPort()));
                row.createCell(9).setCellValue(link.getProtocol() == null ? "" : link.getProtocol());
                row.createCell(10).setCellValue(link.getAuthMethod() == null ? "" : link.getAuthMethod());
                row.createCell(11).setCellValue(link.getScenario() == null ? "" : link.getScenario());
                row.createCell(12).setCellValue(link.getDescription() == null ? "" : link.getDescription());
                row.createCell(13).setCellValue(link.getOwner() == null ? "" : link.getOwner());
                row.createCell(14).setCellValue(link.getStatus() == null ? "" : link.getStatus());
                row.createCell(15).setCellValue(link.getMonitoringLevel() == null ? "" : link.getMonitoringLevel());
                row.createCell(16).setCellValue(link.getCreatedTime() == null ? "" : dt.format(link.getCreatedTime()));
                row.createCell(17).setCellValue(link.getUpdatedTime() == null ? "" : dt.format(link.getUpdatedTime()));
                row.createCell(18).setCellValue(link.getCreatedBy() == null ? "" : link.getCreatedBy());
                row.createCell(19).setCellValue(link.getUpdatedBy() == null ? "" : link.getUpdatedBy());
            }
            
            // 自动调整列宽
            for (int i = 0; i < titles.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 生成文件名（包含筛选条件信息）
            String fileName = generateFileName(sourceEnv, targetEnv, status, protocol, keyword);
            
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            
            workbook.write(output);
            output.flush();
        }
    }
    
    /**
     * 生成文件名（包含筛选条件信息）
     * 格式：信贷系统外部链接网络管理清单_{筛选条件}_{日期}.xlsx
     */
    private String generateFileName(String sourceEnv, String targetEnv, 
                                     String status, String protocol, String keyword) {
        StringBuilder fileName = new StringBuilder("信贷系统外部链接网络管理清单");
        List<String> conditions = new java.util.ArrayList<>();
        
        if (sourceEnv != null && !sourceEnv.isEmpty() && !"all".equals(sourceEnv)) {
            conditions.add("源环境-" + getEnvName(sourceEnv));
        }
        if (targetEnv != null && !targetEnv.isEmpty() && !"all".equals(targetEnv)) {
            conditions.add("目标环境-" + getEnvName(targetEnv));
        }
        if (status != null && !status.isEmpty() && !"all".equals(status)) {
            conditions.add("状态-" + getStatusName(status));
        }
        if (protocol != null && !protocol.isEmpty() && !"all".equals(protocol)) {
            conditions.add("协议-" + protocol);
        }
        if (keyword != null && !keyword.isEmpty()) {
            conditions.add("关键字-" + keyword);
        }
        
        if (!conditions.isEmpty()) {
            fileName.append("_").append(String.join("-", conditions));
        }
        
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        fileName.append("_").append(dateStr).append(".xlsx");
        
        return fileName.toString();
    }
    
    /**
     * 获取环境显示名称
     */
    private String getEnvName(String envCode) {
        switch (envCode) {
            case "prod": return "生产";
            case "uat": return "UAT";
            case "sit": return "SIT";
            case "dev": return "开发";
            default: return envCode;
        }
    }
    
    /**
     * 获取状态显示名称
     */
    private String getStatusName(String statusCode) {
        switch (statusCode) {
            case "active": return "启用中";
            case "testing": return "测试中";
            case "disabled": return "已停用";
            default: return statusCode;
        }
    }
}
