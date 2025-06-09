package com.aiguibin.core.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 *
 */
public class FileAccessor {


    //日志声明
    private static final Log logger = LogFactory.getLog(FileAccessor.class);

    // 获取项目根目录（IDE中通常是项目根路径，打包后可能是JAR所在目录）
    private static final String projectRoot = System.getProperty("user.dir");

    public static Path getProjectRootFolderPath(String folderName) {
        // 拼接自定义文件夹路径
        Path projectRootFolderPath=Paths.get(projectRoot, folderName);
        return projectRootFolderPath;
    }


    /**
     * 确保目录存在，不存在则创建
     */
    public static void ensureDirectoryExists(String directoryPath) throws IOException {
        Path dir = Paths.get(directoryPath);
        if (Files.notExists(dir)) {
            // 检查文件夹是否存在
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("警告：%s目录不存在！", dir));
            }
            Files.createDirectories(dir);
            logger.info("目录已创建: " + directoryPath);
        }
    }

    /**
     * 检查文件是否存在，不存在则记录日志
     */
    public static boolean checkFileExists(String filePath) {
        Path path = Paths.get(filePath);
        boolean exists = Files.exists(path);
        if (!exists && logger.isDebugEnabled()) {
            logger.debug("文件不存在: " + filePath);
        }
        return exists;
    }
    /**
     * 公共递归遍历方法（支持任意嵌套结构）
     * @param dir 要遍历的根目录
     * @param processor 文件处理逻辑
     */
    public static void traverseDirectory(File dir, Consumer<File> processor) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // 递归处理子目录（如0509、0510、subdir等）
                traverseDirectory(file, processor);
            } else {
                // 处理符合要求的文件
                processor.accept(file);
            }
        }
    }

    public static void copyDirectory(String sourceDirectory, String targetDirectory) throws IOException {
        Path source = getProjectRootFolderPath(sourceDirectory);
        Path target = getProjectRootFolderPath(targetDirectory);

        if (!Files.exists(source)) {
            throw new IOException("源目录不存在: " + source);
        }

        // 清空目标目录（如果存在）
        if (Files.exists(target)) {
            FileUtils.cleanDirectory(target.toFile());
        } else {
            Files.createDirectories(target);
        }

        // 执行复制
        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(src -> {
                try {
                    Path dest = target.resolve(source.relativize(src));
                    if (Files.isDirectory(src)) {
                        Files.createDirectories(dest);
                    } else {
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("复制失败: " + src, e);
                }
            });
        }
    }
    /**
     * 清空指定目录下的所有文件和子目录
     *
     * @param directory 要清空的目录路径
     * @param recursive 是否递归清除子目录
     * @throws IOException 当文件删除失败时抛出
     */
    public static void clearDirectory(Path directory, boolean recursive) throws IOException {
        if (!Files.exists(directory)) {
            logger.warn("目录不存在: " + directory);
            return;
        }

        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException("路径不是目录: " + directory);
        }

        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                // 如果是递归模式，删除所有子目录（除了根目录）
                if (recursive && !dir.equals(directory)) {
                    Files.delete(dir);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        logger.info("成功清空目录: " + directory + (recursive ? "（含子目录）" : ""));
    }
}
