package com.aiguibin.core.excel;

import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ExcelCaseInsensitiveMatcher {

    // 配置常量（关键修改：H列→索引7，B列→索引1）
    private static final Path SOURCE_PATH = Paths.get("F:\\Desktop\\source.xlsx");
    private static final Path TARGET_DIR = Paths.get("F:\\Desktop\\together");
    private static final Path OUTPUT_PATH = Paths.get("F:\\Desktop\\output1.txt");
    private static final String DELIMITER = "\t";
    private static final Set<String> EXCLUDED_FIELDS = new HashSet<>(Arrays.asList("经办人", "经办机构"));
    private static final DataFormatter dataFormatter = new DataFormatter();

    // 有序存储结构（保持源文件H列顺序）
    private static class FieldGroup {
        String hValue;  // 源文件H列值
        List<String> lines = new ArrayList<>();

        FieldGroup(String hValue) {
            this.hValue = hValue;
        }
    }

    public static void main(String[] args) {
        try {
            // 1. 加载源文件H列（索引7）建立有序索引
            LinkedHashMap<String, FieldGroup> fieldGroups = loadSourceOrder();
            log("源文件加载完成，有效H列字段数：" + fieldGroups.size());

            // 2. 流式处理目标文件B列（索引1）
            processTargetFiles(fieldGroups);
            log("目标文件处理完成");

            // 3. 生成有序输出（格式：Sheet|B列|D列|C列）
            generateOrderedOutput(fieldGroups);
            log("输出文件生成完毕：" + OUTPUT_PATH);

        } catch (Exception e) {
            logError("处理过程发生错误", e);
        }
    }

    // 核心修改1：加载源文件H列（索引7）
    private static LinkedHashMap<String, FieldGroup> loadSourceOrder() throws Exception {
        LinkedHashMap<String, FieldGroup> groups = new LinkedHashMap<>();
        try (Workbook workbook = WorkbookFactory.create(SOURCE_PATH.toFile())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                String hValue = getCellValue(row, 7); // H列索引7
                if (!hValue.isEmpty() && !EXCLUDED_FIELDS.contains(hValue)) {
                    groups.put(hValue, new FieldGroup(hValue));
                }
            }
        }
        return groups;
    }

    // 处理目标文件（带进度日志）
    private static void processTargetFiles(LinkedHashMap<String, FieldGroup> groups) {
        AtomicInteger fileCount = new AtomicInteger(0);
        try (Stream<Path> paths = Files.walk(TARGET_DIR)) {
            paths.filter(ExcelCaseInsensitiveMatcher::isValidExcelFile)
                    .forEach(file -> {
                        processFile(file, groups);
                        log("已处理文件：" + file + " (" + fileCount.incrementAndGet() + ")");
                    });
        } catch (IOException e) {
            logError("目录遍历失败", e);
        }
    }

    // 处理单个文件
    private static void processFile(Path file, LinkedHashMap<String, FieldGroup> groups) {
        try (Workbook workbook = WorkbookFactory.create(file.toFile())) {
            for (Sheet sheet : workbook) {
                String sheetName = sheet.getSheetName();
                for (Row row : sheet) {
                    processRow(row, sheetName, groups);
                }
            }
        } catch (Exception e) {
            logError("文件处理失败：" + file, e);
        }
    }

    // 处理单行数据
    private static void processRow(Row row, String sheetName, LinkedHashMap<String, FieldGroup> groups) {
        String bValue = getCellValue(row, 1); // B列索引1（匹配键）
        FieldGroup group = groups.get(bValue);
        if (group != null) {
            // 输出列：Sheet名称（sheetName）、B列值（bValue）、D列值（索引3）、C列值（索引2）
            String line = String.join(DELIMITER,
                    sheetName,
                    bValue,  // B列值
                    getCellValue(row, 3),  // D列值（索引3）
                    getCellValue(row, 2)   // C列值（索引2）
            );
            synchronized (group) {
                group.lines.add(line);
            }
        }
    }

    // 输出格式保持不变（仅数据来源变更）
    private static void generateOrderedOutput(LinkedHashMap<String, FieldGroup> groups) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(OUTPUT_PATH)) {
            writer.write("Sheet名称" + DELIMITER + "B列值"
                    + DELIMITER + "D列值" + DELIMITER + "C列值");
            writer.newLine();

            groups.values().forEach(group -> {
                group.lines.forEach(line -> {
                    try {
                        writer.write(line);
                        writer.newLine();
                    } catch (IOException e) {
                        logError("写入失败：" + line, e);
                    }
                });
            });
        }
    }

    // 日志方法
    private static void log(String message) {
        System.out.printf("[%tT] INFO - %s%n", new Date(), message);
    }

    private static void logError(String message, Throwable e) {
        System.err.printf("[%tT] ERROR - %s : %s%n", new Date(), message, e.getMessage());
        e.printStackTrace(System.err);
    }

    // 辅助方法
    private static String getCellValue(Row row, int column) {
        Cell cell = row.getCell(column, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell != null ? dataFormatter.formatCellValue(cell).trim() : "";
    }

    private static boolean isValidExcelFile(Path path) {
        if (!Files.isRegularFile(path)) return false;
        String name = path.getFileName().toString();
        return !name.startsWith("~$") && (name.endsWith(".xls") || name.endsWith(".xlsx"));
    }
}
