package com.aiguibin.core.excel;



import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.atomic.AtomicInteger;


public class ExcelFileMergeCollector {


    public static void main(String[] args) {
        Path source = Paths.get("E:\\Desktop\\database");
        Path targetDir = Paths.get("E:\\Desktop\\together");

        try {
            // 创建目标目录（如果不存在）
            Files.createDirectories(targetDir);

            // 使用原子计数器统计复制文件数量
            AtomicInteger counter = new AtomicInteger();

            // 遍历文件树
            Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (isExcelFile(file)) {
                        copyFileWithUniqueName(file, targetDir);
                        counter.incrementAndGet();
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    System.err.println("无法访问文件: " + file + " - " + exc);
                    return FileVisitResult.CONTINUE;
                }
            });

            System.out.println("复制完成，共复制 " + counter.get() + " 个Excel文件");
        } catch (IOException e) {
            System.err.println("发生错误: " + e.getMessage());
        }
    }

    private static boolean isExcelFile(Path file) {
        String fileName = file.getFileName().toString().toLowerCase();
        return fileName.endsWith(".xls") || fileName.endsWith(".xlsx");
    }

    private static void copyFileWithUniqueName(Path sourceFile, Path targetDir) throws IOException {
        String originalName = sourceFile.getFileName().toString();
        String baseName = originalName.substring(0, originalName.lastIndexOf('.'));
        String extension = originalName.substring(originalName.lastIndexOf('.'));

        Path targetFile = targetDir.resolve(originalName);
        int copyCount = 1;

        // 处理重名文件
        while (Files.exists(targetFile)) {
            String newName = baseName + "_" + copyCount + extension;
            targetFile = targetDir.resolve(newName);
            copyCount++;
        }

        Files.copy(sourceFile, targetFile);
    }
}