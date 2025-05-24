package com.aiguibin.core.converter;

import com.aiguibin.core.common.ExcelHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 递归遍历文件夹，把所有Excel文件都归集到一个目录
 */

public class ExcelFileCollector {


    public static final Log logger = LogFactory.getLog(ExcelFileCollector.class);

    public static void main(String[] args) throws IOException {
        // 源目录路径
        Path sourceDir = Paths.get("F:\\Desktop\\AD-天阳架构实施之数据库设计");
        // 目标汇总目录
        Path targetDir = Paths.get("F:\\Desktop\\checkedExcel\\tableList\\");

        // 创建目标目录（如果不存在）
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 使用文件树遍历
        Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                    throws IOException {
                // 跳过目标目录自身
                if (dir.equals(targetDir)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                    throws IOException {
                // 过滤Excel文件
                String fileName = file.getFileName().toString().toLowerCase();
                if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
                    // 处理重复文件名
                    String originalName = file.getFileName().toString();
                    Path destFile = generateUniquePath(targetDir, originalName);

                    // 执行文件复制
                    Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        logger.debug("Excel文件收集完成！");
    }

    // 生成唯一文件名的方法
    private static Path generateUniquePath(Path targetDir, String originalName) {
        int dotIndex = originalName.lastIndexOf('.');
        String baseName = (dotIndex == -1) ? originalName : originalName.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : originalName.substring(dotIndex);

        Path destination = targetDir.resolve(originalName);
        int counter = 0;

        // 循环直到生成唯一文件名
        while (Files.exists(destination)) {
            counter++;
            String newName = baseName + "(" + counter + ")" + extension;
            destination = targetDir.resolve(newName);
        }
        return destination;
    }
}