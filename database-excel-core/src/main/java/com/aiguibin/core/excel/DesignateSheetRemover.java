package com.aiguibin.core.excel;


import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;


public class DesignateSheetRemover {

    private static final Set<String> SHEETS_TO_REMOVE = new HashSet<>(Arrays.asList(
            "修订记录","修改记录","文档修订记录", "ER图","对公ER图","个人ER图", "目录", "初始化表","索引列表","索引"
    ));

    public static void main(String[] args) {
        Path targetDir = Paths.get("E:\\Desktop\\together");

        try {
            Files.walk(targetDir)
                    .filter(DesignateSheetRemover::isExcelFile)
                    .forEach(DesignateSheetRemover::processExcelFile);

            System.out.println("处理完成！");
        } catch (IOException e) {
            System.err.println("遍历文件错误: " + e.getMessage());
        }
    }

    private static boolean isExcelFile(Path file) {
        String fileName = file.toString().toLowerCase();
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    private static void processExcelFile(Path file) {
        try {
            // 读取工作簿
            Workbook workbook = WorkbookFactory.create(Files.newInputStream(file));

            // 收集需要删除的sheet索引
            List<Integer> sheetsToDelete = new ArrayList<>();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetName(i);
                if (SHEETS_TO_REMOVE.contains(sheetName)) {
                    sheetsToDelete.add(i);
                }
            }

            // 逆序删除避免索引变化
            Collections.sort(sheetsToDelete, Collections.reverseOrder());
            sheetsToDelete.forEach(workbook::removeSheetAt);

            // 保存修改
            try (OutputStream out = Files.newOutputStream(file, StandardOpenOption.TRUNCATE_EXISTING)) {
                workbook.write(out);
            }

            System.out.printf("已处理 %s，删除 %d 个Sheet%n",
                    file.getFileName(), sheetsToDelete.size());

            workbook.close();
        } catch (Exception e) {
            System.err.printf("处理 %s 失败：%s%n", file.getFileName(), e.getMessage());
        }
    }
}