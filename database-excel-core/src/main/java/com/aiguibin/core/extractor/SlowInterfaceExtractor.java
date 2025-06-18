package com.aiguibin.core.extractor;

import com.aiguibin.core.common.ExcelHelper;
import com.aiguibin.core.common.FileAccessor;
import com.aiguibin.core.dictionary.InterfaceManageDict;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import java.util.List;
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
    private static final String SLOW_INTERFACE_STEP_TWO_EXCEL = "database-excel-core/docs/stageList/stepTwoSlowInterface";
    private static final String TASK_LIST_FILE_EXCEL = "database-excel-core/docs/taskerList/slowInterfaceTaskFileList.xlsx";

    // 日志解析配置
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{.*}");
    // 匹配新日志格式的正则表达式
    private static final Pattern TEXT_PATTERN = Pattern.compile("交易URL:(.*?)，交易耗时：(\\d+)ms");

    private static final Pattern ENV_PATTERN = Pattern.compile("stepOneSlowInterface/([^/]+)");

    private static final String[] EXCEL_HEADERS = {"序号", "请求方法", "服务中心", "接口地址", "耗时(ms)", "能力中心中文", "负责人英文", "负责人中文", "所属环境"};

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 禅道任务配置
    private static final String[] TASK_HEADERS = {
            "序号", "所属执行", "任务类型", "指派给", "任务名称", "任务描述", "预计开始日期", "预计结束日期", "预计工时（小时）", "优先级（1-4）"
    };


    public static void extractAllCompressedFiles(Path sourceDir, Path destDir) throws IOException {
        logger.info("解压文件开始: " + sourceDir);
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

        // 创建临时工作簿
        Path tempExcel = Files.createTempFile("log_processor_temp_", ".xlsx");
        // 初始化Excel工作簿
        Workbook tempWorkbook = initWorkbook(tempExcel);
        Sheet tempSheet  = tempWorkbook.getSheet("result");

        // 获取初始序号
        int serialNumber = tempSheet.getLastRowNum();

        // 处理所有日志文件
        for (Path logFile : logFiles) {
            processLogFile(logFile, tempSheet, ++serialNumber);
            // 更新序号基准为当前sheet的最后行号
            serialNumber = tempSheet.getLastRowNum();
        }

        // 保存临时工作簿
        ExcelHelper.saveWorkbook(tempWorkbook, tempExcel);
        tempWorkbook.close();

        // 使用高效去重方法处理大文件
        ExcelHelper.removeDuplicatesInLargeFile(
                tempExcel.toString(),      // 输入文件（临时文件）
                excelOutput.toString(),    // 输出文件（最终结果）
                0,                        // 工作表索引（第一个sheet）
                new int[]{2, 3}           // 需要去重的列索引
        );

        // 删除临时文件
        Files.deleteIfExists(tempExcel);
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
    private static Workbook initWorkbook(Path excelPath) {
        Workbook workbook = new SXSSFWorkbook(1000);
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

        String environment;
        String logFileStr = String.valueOf(logPath);
        // 统一替换路径分隔符为正斜杠（避免不同系统差异）
        String normalizedPath = logFileStr.replace('\\', '/');
        Matcher matcher = ENV_PATTERN.matcher(normalizedPath);
        if (matcher.find()) {
            environment = matcher.group(1).toUpperCase() + "环境：(҂‾▵‾)︻デ═一  " + logPath.getFileName().toString(); // 返回捕获组（dev/sit等）
        } else {
            throw new IllegalArgumentException("Pattern 'stepOneSlowInterface' not found in path");
        }
        try (Stream<String> lines = Files.lines(logPath)) {
            lines.forEach(line -> {
                try {
                    Matcher jsonMatcher = JSON_PATTERN.matcher(line);
                    Matcher textMatcher = TEXT_PATTERN.matcher(line);

                    if (jsonMatcher.find()) {
                        // 解析JSON日志行
                        LogEntry entry = parseJsonLogEntry(jsonMatcher.group(), environment);
                        InterfaceManageDict.InterfaceInfo interfaceManageInfo = InterfaceManageDict.getInterfaceInfo(entry.serviceCenter);
                        // 线程安全写入Excel
                        synchronized (sheet) {
                            addRowToSheet(sheet, rowIndex, serialNumber, entry, interfaceManageInfo);
                        }
                    } else if (textMatcher.find()) {
                        // 解析新格式日志行
                        String url = textMatcher.group(1).trim();
                        String duration = textMatcher.group(2).trim();
                        LogEntry entry = parseTextLogEntry(url, duration, environment);
                        InterfaceManageDict.InterfaceInfo interfaceManageInfo = InterfaceManageDict.getInterfaceInfo(entry.serviceCenter);
                        // 线程安全写入Excel
                        synchronized (sheet) {
                            addRowToSheet(sheet, rowIndex, serialNumber, entry, interfaceManageInfo);
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
     * 添加行到Excel表
     */
    private static void addRowToSheet(Sheet sheet, AtomicInteger rowIndex, AtomicInteger serialNumber,
                                      LogEntry entry, InterfaceManageDict.InterfaceInfo interfaceManageInfo) {
        if (Integer.parseInt(entry.duration)>10000){
            Row row = sheet.createRow(rowIndex.getAndIncrement());
            row.createCell(0).setCellValue(serialNumber.getAndIncrement());
            row.createCell(1).setCellValue(entry.method);
            row.createCell(2).setCellValue(entry.serviceCenter);
            row.createCell(3).setCellValue(entry.interfacePath);
            row.createCell(4).setCellValue(entry.duration);
            row.createCell(5).setCellValue(interfaceManageInfo.getCenterCnName());
            row.createCell(6).setCellValue(interfaceManageInfo.getSuperintendentEnName());
            row.createCell(7).setCellValue(interfaceManageInfo.getSuperintendentCnName());
            row.createCell(8).setCellValue(entry.environment);
        }
    }


    /**
     * 日志条目解析 - 使用Jackson解析JSON
     */
    private static LogEntry parseJsonLogEntry(String json, String environment) throws IOException {
        JsonNode node = objectMapper.readTree(json);

        String method = node.path("method").asText();
        String path = node.path("path").asText();
        String duration = node.path("duration").asText().replaceAll("\\D+", "");

        // 解析路径（示例：/service-center/api/resource）
        String[] parts = path.replaceFirst("^/", "").split("/", 2);
        String serviceCenter = parts.length > 0 ? parts[0] : "";
        String interfacePath = parts.length > 1 ? "/" + parts[1] : "/";

        return new LogEntry(method, serviceCenter, interfacePath, duration, environment);
    }

    /**
     * 解析新格式日志条目
     */
    private static LogEntry parseTextLogEntry(String url, String duration, String environment) {
        // 解析URL路径（示例：/tansun-tcp-system-boot/role/saveRoleMuen）
        String[] parts = url.replaceFirst("^/", "").split("/", 2);
        String serviceCenter = parts.length > 0 ? parts[0] : "";
        String interfacePath = parts.length > 1 ? "/" + parts[1] : "/";

        // 新格式无请求方法，设为空字符串
        return new LogEntry("POST", serviceCenter, interfacePath, duration, environment);
    }

    /**
     * 日志条目数据结构
     */
    private static class LogEntry {
        String method;
        String serviceCenter;
        String interfacePath;
        String duration;
        String environment;

        public LogEntry(String method, String serviceCenter, String interfacePath, String duration, String environment) {
            this.method = method;
            this.serviceCenter = serviceCenter;
            this.interfacePath = interfacePath;
            this.duration = duration;
            this.environment = environment;
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
        if (!ExcelHelper.isExcelFile(Paths.get(file.getAbsolutePath()))) {
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
                logger.error("Excel中找不到'result'工作表");
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
                String centerCnName = ExcelHelper.getCellValueAsString(row.getCell(5));
                String superintendentEnName = ExcelHelper.getCellValueAsString(row.getCell(6));
                String superintendentCnName = ExcelHelper.getCellValueAsString(row.getCell(7));
                String environment = ExcelHelper.getCellValueAsString(row.getCell(8));

                // 构建任务数据
                List<String> taskRow = new ArrayList<>();
                taskRow.add(serialNum);                          // 序号
                taskRow.add("系统开发");                           // 所属执行
                taskRow.add("开发");                               // 任务类型
                taskRow.add(superintendentEnName);                // 指派给
                taskRow.add(centerCnName + ": " + serviceCenter + interfacePath + ",责任人：" + superintendentCnName + "--性能优化-耗时：" + duration + "ms, 序号：" + formatCurrentDate + "--" + i); // 任务名称
                taskRow.add("接口详情 ▅︻┳┷═一\r\n"
                        + " ▎✌---请求方法: " + method + "\r\n"
                        + " ▎✌---服务中心: " + centerCnName + "\r\n"
                        + " ▎✌---接口地址: " + serviceCenter + interfacePath + "\r\n"
                        + " ▎✌---响应耗时: " + duration + "ms \r\n"
                        + " ▎✌---SVN路径地址：98-工作区/09-开发组/01-开发实施组/08-评审管理/非功能优化 \r\n"
                        + " ▎✌---日志文件路径：" + environment);
                taskRow.add(formatCurrentDate);                  // 预计开始日期
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


    public static void main(String[] args) {
        try {
            // 清空解压文件
            FileAccessor.clearDirectory(FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_ONE_PATH), false);
            // 清空生成的Excel
            FileAccessor.clearDirectory(FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_TWO_EXCEL), false);
            // 复制已解压的文件到指定目录
            FileAccessor.moveFilesByPattern(
                    FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_ROOT_PATH),
                    FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_ONE_PATH), "*.log");

            FileAccessor.deleteFilesByPattern(FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_ROOT_PATH), "gateway-project-*.*.zip");
            // 步骤1: 解压所有压缩文件
            extractAllCompressedFiles(
                    FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_ROOT_PATH),
                    FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_ONE_PATH)
            );

            // 步骤2: 处理所有日志文件并生成Excel
            Path excelOutputPath = FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_TWO_EXCEL + "/slow_interfaces.xlsx");
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