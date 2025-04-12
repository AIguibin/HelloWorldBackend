package com.aiguibin.core.converter;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 转换慢接口到excel,控制一周一个，分周创建文件夹，不区分环境
 * 运行 LogToExcelConverter.java 输出 slow_interface.xlsx
 * 手动去重 标记组长
 * 发送给江山入禅道任务
 */
public class LogToExcelConverter {

    private static final Pattern JSON_PATTERN = Pattern.compile("\\{.*}");
    private static final String ROOT_PATH = "F:\\Desktop\\非功能优化\\慢接口优化\\";
    private static final String EXCEL_FILE = "F:\\Desktop\\checkedExcel\\" + "slow_interface.xlsx";

    public static void main(String[] args) {
        try {
            // 获取所有日志文件
            List<Path> logFiles = findLogFiles(Paths.get(ROOT_PATH));

            // 初始化或加载Excel文件
            Workbook workbook = initWorkbook();
            Sheet sheet = workbook.getSheet("慢接口统计");

            // 获取初始序号
            int serialNumber = sheet.getLastRowNum(); // 0-based

            // 处理所有日志文件
            for (Path logFile : logFiles) {
                processLogFile(logFile, sheet, ++serialNumber);
            }

            // 写入Excel并关闭
            try (FileOutputStream fos = new FileOutputStream(EXCEL_FILE)) {
                workbook.write(fos);
            }
            workbook.close();

            System.out.println("Excel文件生成完成：" + EXCEL_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Path> findLogFiles(Path rootDir) throws IOException {
        try (Stream<Path> paths = Files.walk(rootDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".log"))
                    .collect(Collectors.toList());
        }
    }

    private static Workbook initWorkbook() throws IOException {
        File excelFile = new File(EXCEL_FILE);
        Workbook workbook;

        if (excelFile.exists()) {
            workbook = new XSSFWorkbook(new FileInputStream(excelFile));
        } else {
            workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("慢接口统计");
            createHeaderRow(sheet);
        }
        return workbook;
    }

    private static void createHeaderRow(Sheet sheet) {
        String[] headers = {"序号", "请求方法", "服务中心", "接口地址", "耗时(ms)"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
    }

    private static void processLogFile(Path logPath, Sheet sheet, int startSerial) {
        ObjectMapper mapper = new ObjectMapper();
        final int[] rowIndex = {sheet.getLastRowNum() + 1};
        final int[] serialNumber = {startSerial};

        try {
            Files.lines(logPath).forEach(line -> {
                try {
                    Matcher matcher = JSON_PATTERN.matcher(line);
                    if (matcher.find()) {
                        JsonNode node = mapper.readTree(matcher.group());

                        // 提取数据
                        String method = node.path("method").asText();
                        String path = node.path("path").asText();
                        String duration = node.path("duration").asText().replaceAll("\\D+", "");

                        // 解析路径
                        String[] pathParts = path.replaceFirst("^/", "").split("/");
                        String serviceCenter = pathParts.length > 0 ? pathParts[0] : "";
                        String interfacePath = pathParts.length > 1 ?
                                "/" + String.join("/", Arrays.copyOfRange(pathParts, 1, pathParts.length)) : "";

                        // 写入Excel
                        synchronized (sheet) {
                            Row row = sheet.createRow(rowIndex[0]++);
                            row.createCell(0).setCellValue(serialNumber[0]++);
                            row.createCell(1).setCellValue(method);
                            row.createCell(2).setCellValue(serviceCenter);
                            row.createCell(3).setCellValue(interfacePath);
                            row.createCell(4).setCellValue(duration);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("解析失败: " + line);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}