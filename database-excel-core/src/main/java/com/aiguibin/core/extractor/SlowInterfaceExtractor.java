package com.aiguibin.core.extractor;

import com.aiguibin.core.common.ExcelHelper;
import com.aiguibin.core.common.FileAccessor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class SlowInterfaceExtractor {

    private static final Log logger = LogFactory.getLog(SlowInterfaceExtractor.class);

    // 路径配置
    private static final String SLOW_INTERFACE_ROOT_PATH = "database-excel-core/docs/gateway";
    private static final String SLOW_INTERFACE_STEP_ONE_PATH = "database-excel-core/docs/stageList/stepOneSlowInterface";
    private static final String SLOW_INTERFACE_STEP_TWO_EXCEL = "database-excel-core/docs/stageList/stepTwoSlowInterface/slow_interfaces.xlsx";
    private static final String TASK_LIST_FILE_EXCEL = "database-excel-core/docs/taskerList/slowInterfaceTaskFileList.xlsx";

    // 日志解析配置
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{.*}");
    private static final String[] EXCEL_HEADERS = {"序号", "请求方法", "服务中心", "接口地址", "耗时(ms)", "能力中心", "负责人"};
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 禅道任务配置
    private static final String[] TASK_HEADERS = {
            "序号", "所属执行", "任务类型", "指派给", "任务名称", "任务描述", "预计开始日期", "预计结束日期", "预计工时（小时）", "优先级（1-4）"
    };


    public static void extractAllCompressedFiles(Path sourceDir, Path destDir) throws IOException {
        Files.walk(sourceDir)
                .filter(Files::isRegularFile)
                .filter(path -> isSupportedArchive(path))
                .forEach(archive -> {
                    try {
                        Path relativePath = sourceDir.relativize(archive.getParent());
                        Path outputDir = destDir.resolve(relativePath);
                        Files.createDirectories(outputDir);

                        if (archive.toString().endsWith(".zip")) {
                            unzipFile(archive, outputDir);
                        } else if (archive.toString().endsWith(".tar") || archive.toString().endsWith(".tar.gz")) {
                            extractTarFile(archive, outputDir);
                        }
                    } catch (IOException e) {
                        logger.error("Error processing " + archive + ": " + e.getMessage(), e);
                    }
                });
    }

    private static boolean isSupportedArchive(Path path) {
        String name = path.toString().toLowerCase();
        return name.endsWith(".zip") || name.endsWith(".tar") || name.endsWith(".tar.gz");
    }

    private static void unzipFile(Path zipFile, Path destDir) throws IOException {
        AtomicInteger counter = new AtomicInteger(1);
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.isDirectory()) continue;
                String baseName = getBaseName(zipFile.getFileName().toString());
                String targetFileName = baseName + ".log";
                Path targetPath = getUniquePath(destDir, targetFileName, counter);
                Files.createDirectories(targetPath.getParent());
                Files.copy(zis, targetPath);
            }
        }
    }

    private static void extractTarFile(Path tarFile, Path destDir) throws IOException {
        AtomicInteger counter = new AtomicInteger(1);
        InputStream inputStream = Files.newInputStream(tarFile);

        if (tarFile.toString().endsWith(".gz")) {
            inputStream = new GzipCompressorInputStream(inputStream);
        }

        try (TarArchiveInputStream tis = new TarArchiveInputStream(inputStream)) {
            TarArchiveEntry entry;
            while ((entry = tis.getNextTarEntry()) != null) {
                if (entry.isDirectory()) continue;
                String baseName = getBaseName(tarFile.getFileName().toString());
                String targetFileName = baseName + ".log";
                Path targetPath = getUniquePath(destDir, targetFileName, counter);
                Files.createDirectories(targetPath.getParent());
                Files.copy(tis, targetPath);
            }
        }
    }

    private static String getBaseName(String fileName) {
        if (fileName.endsWith(".tar.gz")) return fileName.substring(0, fileName.length() - 7);
        if (fileName.endsWith(".zip") || fileName.endsWith(".tar")) return fileName.substring(0, fileName.length() - 4);
        return fileName;
    }

    private static Path getUniquePath(Path baseDir, String fileName, AtomicInteger counter) {
        Path original = baseDir.resolve(fileName);
        if (!Files.exists(original)) return original;

        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.'));

        while (true) {
            String newName = baseName + "_" + counter.getAndIncrement() + extension;
            Path newPath = baseDir.resolve(newName);
            if (!Files.exists(newPath)) return newPath;
        }
    }

    /**
     * 处理所有日志文件并生成Excel
     */
    public static void processLogFilesToExcel(Path logDir, Path excelOutput) throws Exception {
        // 获取所有日志文件
        List<Path> logFiles = findLogFiles(logDir);

        // 初始化Excel工作簿
        Workbook workbook = initWorkbook(excelOutput);
        Sheet sheet = workbook.getSheet("result");

        // 获取初始序号
        int serialNumber = sheet.getLastRowNum();

        // 处理所有日志文件
        for (Path logFile : logFiles) {
            processLogFile(logFile, sheet, ++serialNumber);
            // 更新序号基准为当前sheet的最后行号
            serialNumber = sheet.getLastRowNum();
        }

        // 保存Excel文件
        ExcelHelper.saveWorkbook(workbook, excelOutput);
        workbook.close();
        logger.info("Excel文件生成完成: " + excelOutput);
    }

    /**
     * 查找目录下所有日志文件
     */
    private static List<Path> findLogFiles(Path rootDir) throws IOException {
        try (Stream<Path> paths = Files.walk(rootDir)) {
            return paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".log"))
                    .collect(Collectors.toList());
        }
    }

    /**
     * 初始化或加载Excel工作簿
     */
    private static Workbook initWorkbook(Path excelPath) throws IOException {
        if (Files.exists(excelPath)) {
            return ExcelHelper.readWorkbook(excelPath);
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("result");
        createHeaderRow(sheet);
        return workbook;
    }

    /**
     * 创建Excel表头
     */
    private static void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < EXCEL_HEADERS.length; i++) {
            headerRow.createCell(i).setCellValue(EXCEL_HEADERS[i]);
        }
    }

    /**
     * 处理单个日志文件
     */
    private static void processLogFile(Path logPath, Sheet sheet, int startSerial) {
        final AtomicInteger rowIndex = new AtomicInteger(sheet.getLastRowNum() + 1);
        final AtomicInteger serialNumber = new AtomicInteger(startSerial);

        try (Stream<String> lines = Files.lines(logPath)) {
            lines.forEach(line -> {
                try {
                    Matcher matcher = JSON_PATTERN.matcher(line);
                    if (matcher.find()) {
                        // 解析JSON日志行
                        LogEntry entry = parseLogEntry(matcher.group());

                        // 线程安全写入Excel
                        synchronized (sheet) {
                            Row row = sheet.createRow(rowIndex.getAndIncrement());
                            row.createCell(0).setCellValue(serialNumber.getAndIncrement());
                            row.createCell(1).setCellValue(entry.method);
                            row.createCell(2).setCellValue(entry.serviceCenter);
                            row.createCell(3).setCellValue(entry.interfacePath);
                            row.createCell(4).setCellValue(entry.duration);
                        }
                    }
                } catch (Exception e) {
                    logger.debug("解析失败: " + line + " | 错误: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            logger.error("文件读取失败: " + logPath, e);
        }
    }

    /**
     * 日志条目解析 - 使用Jackson解析JSON
     */
    private static LogEntry parseLogEntry(String json) throws IOException {
        JsonNode node = objectMapper.readTree(json);

        String method = node.path("method").asText();
        String path = node.path("path").asText();
        String duration = node.path("duration").asText().replaceAll("\\D+", "");

        // 解析路径（示例：/service-center/api/resource）
        String[] parts = path.replaceFirst("^/", "").split("/", 2);
        String serviceCenter = parts.length > 0 ? parts[0] : "";
        String interfacePath = parts.length > 1 ? "/" + parts[1] : "/";

        return new LogEntry(method, serviceCenter, interfacePath, duration);
    }

    /**
     * 日志条目数据结构
     */
    private static class LogEntry {
        String method;
        String serviceCenter;
        String interfacePath;
        String duration;

        public LogEntry(String method, String serviceCenter, String interfacePath, String duration) {
            this.method = method;
            this.serviceCenter = serviceCenter;
            this.interfacePath = interfacePath;
            this.duration = duration;
        }
    }

    /**
     * 创建禅道任务Excel文件
     */
    public static void createZenPathTask(List<List<String>> data) {
        try (FileOutputStream fos = new FileOutputStream(
                String.valueOf(FileAccessor.getProjectRootFolderPath(TASK_LIST_FILE_EXCEL)))) {
            ExcelHelper.exportToExcel(TASK_HEADERS, data, fos);
            logger.info("禅道任务文件生成成功: " + TASK_LIST_FILE_EXCEL);
        } catch (IOException e) {
            logger.error("禅道任务文件生成失败: " + e.getMessage(), e);
        }
    }

    /**
     * 处理慢接口Excel文件并生成禅道任务
     */
    public static void processStepSixFile(File file) {
        logger.debug("正在处理: " + file.getAbsolutePath());
        if (!isExcelFile(Paths.get(file.getAbsolutePath()))) {
            logger.warn("文件不是Excel格式: " + file.getName());
            return;
        }

        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        LocalDate futureDate = currentDate.plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String formatCurrentDate = currentDate.format(formatter);
        String formatFutureDate = futureDate.format(formatter);

        List<List<String>> taskData = new ArrayList<>();

        try {
            Workbook workbook = ExcelHelper.readWorkbook(Paths.get(file.getAbsolutePath()));
            Sheet sheet = workbook.getSheet("result");
            if (sheet == null) {
                logger.error("Excel中找不到'慢接口统计'工作表");
                return;
            }

            // 从第1行开始（跳过表头）
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                // 确保至少有5列数据
                if (row.getLastCellNum() < 5) {
                    logger.warn("行 " + (i + 1) + " 数据不完整，跳过");
                    continue;
                }

                // 提取单元格值
                String serialNum = ExcelHelper.getCellValueAsString(row.getCell(0));
                String method = ExcelHelper.getCellValueAsString(row.getCell(1));
                String serviceCenter = ExcelHelper.getCellValueAsString(row.getCell(2));
                String interfacePath = ExcelHelper.getCellValueAsString(row.getCell(3));
                String duration = ExcelHelper.getCellValueAsString(row.getCell(4));

                // 根据服务中心获取负责人
                String assignee = getAssigneeByServiceCenter(serviceCenter);

                // 构建任务数据
                List<String> taskRow = new ArrayList<>();
                taskRow.add(serialNum);                          // 序号
                taskRow.add("系统开发");                           // 所属执行
                taskRow.add("开发");                               // 任务类型
                taskRow.add(assignee);                           // 指派给
                taskRow.add(serviceCenter + ": " + interfacePath + "--性能优化-耗时：" + duration + "ms, " + i); // 任务名称
                taskRow.add("接口详情:\r\n"
                        + "--请求方法: " + method + "\r\n"
                        + "--服务中心: " + serviceCenter + "\r\n"
                        + "--接口地址: " + interfacePath + "\r\n"
                        + "--响应耗时: " + duration + "ms");         // 任务描述
                taskRow.add(formatCurrentDate);                   // 预计开始日期
                taskRow.add(formatFutureDate);                    // 预计结束日期
                taskRow.add("8");                                // 预计工时（小时）
                taskRow.add("2");                                // 优先级（1-4）

                taskData.add(taskRow);
            }

            // 创建禅道任务文件
            createZenPathTask(taskData);

        } catch (IOException e) {
            logger.error("处理禅道任务失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据服务中心获取负责人
     */
    private static String getAssigneeByServiceCenter(String serviceCenter) {
        // 实际实现应从配置或字典获取
        Map<String, String> assigneeMap = new HashMap<>();
        assigneeMap.put("user-center", "张三");
        assigneeMap.put("order-center", "李四");
        assigneeMap.put("payment-center", "王五");

        return assigneeMap.getOrDefault(serviceCenter.toLowerCase(), "默认负责人");
    }

    /**
     * 检查文件是否为Excel
     */
    private static boolean isExcelFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".xlsx") || fileName.endsWith(".xls");
    }


    public static void main(String[] args) {
        try {

            FileAccessor.clearDirectory(FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_ONE_PATH), false);
            FileAccessor.clearDirectory(FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_TWO_EXCEL), false);
            // 步骤1: 解压所有压缩文件
            extractAllCompressedFiles(
                    FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_ROOT_PATH),
                    FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_ONE_PATH)
            );

            // 步骤2: 处理所有日志文件并生成Excel
            Path excelOutputPath = FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_TWO_EXCEL);
            processLogFilesToExcel(
                    FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_ONE_PATH),
                    excelOutputPath
            );

            logger.info("慢接口数据提取完成！");

            // 步骤3: 生成禅道任务
            processStepSixFile(excelOutputPath.toFile());

        } catch (Exception e) {
            logger.error("处理失败: " + e.getMessage(), e);
        }
    }
}