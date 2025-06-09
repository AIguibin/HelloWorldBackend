package com.aiguibin.core.common;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
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
        Path projectRootFolderPath = Paths.get(projectRoot, folderName);
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
     *
     * @param dir       要遍历的根目录
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

    /**
     * 递归移动匹配指定通配符模式的文件到目标目录（平铺结构）
     *
     * @param sourcePath 源目录路径
     * @param targetPath 目标目录路径
     * @param patterns  通配符模式数组（如 "*.log", "2025-*.log"）
     * @throws IOException 如果发生I/O错误
     */
    public static void moveFilesByPattern(Path sourcePath, Path targetPath, String... patterns) throws IOException {
        // 确保目标目录存在
        ensureDirectoryExists(targetPath.toString());

        // 构建PathMatcher列表（支持glob模式）
        List<PathMatcher> matchers = new ArrayList<>();
        for (String pattern : patterns) {
            matchers.add(FileSystems.getDefault().getPathMatcher("glob:" + pattern));
        }

        // 递归遍历源目录
        Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path sourceFile, BasicFileAttributes attrs) throws IOException {
                String fileName = sourceFile.getFileName().toString();

                // 检查是否匹配任意模式
                for (PathMatcher matcher : matchers) {
                    if (matcher.matches(Paths.get(fileName))) {
                        // 生成目标路径（解决重名冲突）
                        Path destFile = generateUniqueFileName(targetPath, fileName);

                        // 移动文件（跨文件系统安全）
                        Files.move(sourceFile, destFile, StandardCopyOption.REPLACE_EXISTING);
                        logger.info("移动文件: " + sourceFile + " -> " + destFile);
                        break; // 匹配一个模式即可
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                logger.error("访问文件失败: " + file, exc);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 生成目标目录中唯一的文件名（避免覆盖）
     *
     * @param targetDir 目标目录
     * @param fileName  原始文件名
     * @return 唯一的文件路径
     */
    private static Path generateUniqueFileName(Path targetDir, String fileName) {
        // 分离文件名和扩展名（如 "app.log" -> "app" + ".log"）
        String baseName;
        String extension = "";
        int dotIndex = fileName.lastIndexOf('.');

        if (dotIndex > 0) {
            baseName = fileName.substring(0, dotIndex);
            extension = fileName.substring(dotIndex);
        } else {
            baseName = fileName;
        }

        // 检查并生成唯一文件名
        Path destFile = targetDir.resolve(fileName);
        int counter = 1;

        while (Files.exists(destFile)) {
            // 使用括号格式：app(1).log 而不是 app.1.log
            fileName = baseName + "(" + counter + ")" + extension;
            destFile = targetDir.resolve(fileName);
            counter++;
        }
        return destFile;
    }

    /**
     * 递归删除指定目录下匹配通配符模式的文件
     *
     * @param sourceDir   源目录路径
     * @param patterns    通配符模式数组（如 "*.tmp", "gateway-project-*.*.zip"）
     * @return            删除的文件数量
     * @throws IOException 如果发生I/O错误
     */
    public static int deleteFilesByPattern(Path sourceDir, String... patterns) throws IOException {
        // 确保源目录存在
        if (!Files.exists(sourceDir)) {
            logger.warn("源目录不存在: " + sourceDir);
            return 0;
        }

        if (!Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("路径不是目录: " + sourceDir);
        }

        // 构建PathMatcher列表（支持glob模式）
        List<PathMatcher> matchers = new ArrayList<>();
        for (String pattern : patterns) {
            matchers.add(FileSystems.getDefault().getPathMatcher("glob:" + pattern));
        }

        // 计数器
        final int[] deletedCount = {0};

        // 递归遍历源目录
        Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();

                // 检查是否匹配任意模式
                for (PathMatcher matcher : matchers) {
                    if (matcher.matches(file.getFileName())) {
                        try {
                            Files.delete(file);
                            deletedCount[0]++;
                            logger.info("已删除文件: " + file);
                        } catch (IOException e) {
                            logger.error("删除文件失败: " + file, e);
                        }
                        break; // 匹配一个模式即可
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                logger.error("访问文件失败: " + file, exc);
                return FileVisitResult.CONTINUE;
            }
        });

        logger.info("共删除 " + deletedCount[0] + " 个匹配文件");
        return deletedCount[0];
    }
}
