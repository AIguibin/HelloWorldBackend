package com.aiguibin.core.excel;

import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class ExcelStreamingTextOutput {

    // 文本输出配置
    private static final String OUTPUT_FILE = "E:\\Desktop\\output.txt";
    private static final String DELIMITER = "\t"; // 使用制表符分隔列
    private static final DataFormatter dataFormatter = new DataFormatter();

    public static void main(String[] args) {
        Path sourcePath = Paths.get("E:\\Desktop\\source.xlsx");
        Path targetDir = Paths.get("E:\\Desktop\\together");

        try {
            // 1. 预加载源文件F列索引
            Set<String> fColumnIndex = loadFColumnIndex(sourcePath);

            // 2. 初始化文本输出流
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(OUTPUT_FILE))) {

                // 写入表头
                writer.write("Sheet名称" + DELIMITER
                        + "B列值" + DELIMITER
                        + "D列值" + DELIMITER
                        + "C列值");
                writer.newLine();

                // 3. 流式处理目标文件
                processTargetFiles(targetDir, fColumnIndex, writer);
            }

        } catch (Exception e) {
            System.err.println("处理过程中发生严重错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 预加载源文件F列值到内存
    private static Set<String> loadFColumnIndex(Path sourcePath) throws Exception {
        Set<String> index = new HashSet<>();
        try (Workbook workbook = WorkbookFactory.create(sourcePath.toFile())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell cell = row.getCell(5); // F列
                if (cell != null) {
                    String value = dataFormatter.formatCellValue(cell).trim();
                    if (!value.isEmpty()) {
                        index.add(value);
                    }
                }
            }
        }
        return index;
    }

    // 处理目标目录下的所有Excel文件
    private static void processTargetFiles(Path targetDir, Set<String> fIndex, BufferedWriter writer) {
        try (Stream<Path> paths = Files.walk(targetDir)) {
            paths.filter(ExcelStreamingTextOutput::isValidExcelFile)
                    .forEach(file -> processSingleFile(file, fIndex, writer));
        } catch (IOException e) {
            System.err.println("遍历目录失败: " + e.getMessage());
        }
    }

    // 处理单个目标文件
    private static void processSingleFile(Path file, Set<String> fIndex, BufferedWriter writer) {
        try (Workbook workbook = WorkbookFactory.create(file.toFile())) {
            for (Sheet sheet : workbook) {
                String sheetName = sheet.getSheetName();
                for (Row row : sheet) {
                    processSingleRow(row, sheetName, fIndex, writer);
                }
            }
        } catch (Exception e) {
            System.err.println("处理文件失败: " + file);
            e.printStackTrace();
        }
    }

    // 处理单行数据
    private static void processSingleRow(Row row, String sheetName,
                                         Set<String> fIndex, BufferedWriter writer) {
        try {
            String dValue = getCellValue(row, 3); // D列
            if (fIndex.contains(dValue)) {
                String bValue = getCellValue(row, 1); // B列
                String cValue = getCellValue(row, 2); // C列

                // 构建输出行
                String outputLine = String.join(DELIMITER,
                        sheetName,
                        bValue,
                        dValue,
                        cValue
                );

                // 立即写入文件
                synchronized (writer) {
                    writer.write(outputLine);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("写入失败: " + e.getMessage());
        }
    }

    // 安全获取单元格值
    private static String getCellValue(Row row, int columnIndex) {
        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return cell != null ? dataFormatter.formatCellValue(cell).trim() : "";
    }

    // 验证是否为有效Excel文件
    private static boolean isValidExcelFile(Path path) {
        if (!Files.isRegularFile(path)) return false;
        String fileName = path.getFileName().toString();
        return !fileName.startsWith("~$") &&  // 排除临时文件
                (fileName.endsWith(".xls") || fileName.endsWith(".xlsx"));
    }
}