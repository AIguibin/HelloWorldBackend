package com.aiguibin.core.extractor;

import com.aiguibin.core.common.FileAccessor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class SlowInterfaceExtractor {

    //日志声明
    private static final Log logger = LogFactory.getLog(SlowInterfaceExtractor.class);

    // 初步筛选慢Interface根路径
    private static final String SLOW_INTERFACE_ROOT_PATH = "database-excel-core/docs/gateway";
    // 慢SQL文件处理
    private static final String SLOW_INTERFACE_STEP_ONE_PATH = "database-excel-core/docs/stageList/stepOneSlowInterface";



    public static void extractAllCompressedFiles(Path sourceDir, Path destDir) throws IOException {
        Files.walk(sourceDir)
                .filter(Files::isRegularFile)
                .filter(path -> isSupportedArchive(path))
                .forEach(archive -> {
                    try {
                        // 获取相对路径（保留原始目录结构）
                        Path relativePath = sourceDir.relativize(archive.getParent());
                        Path outputDir = destDir.resolve(relativePath);
                        Files.createDirectories(outputDir);

                        // 处理压缩文件
                        if (archive.toString().endsWith(".zip")) {
                            unzipFile(archive, outputDir);
                        } else if (archive.toString().endsWith(".tar") || archive.toString().endsWith(".tar.gz")) {
                            extractTarFile(archive, outputDir);
                        }
                    } catch (IOException e) {
                        System.err.println("Error processing " + archive + ": " + e.getMessage());
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

                // 获取基础文件名（去掉压缩扩展名）
                String baseName = getBaseName(zipFile.getFileName().toString());
                // 生成目标文件名（添加.log后缀）
                String targetFileName = baseName + ".log";

                // 创建唯一路径
                Path targetPath = getUniquePath(destDir, targetFileName, counter);
                Files.createDirectories(targetPath.getParent());

                // 复制文件内容
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

                // 获取基础文件名（去掉压缩扩展名）
                String baseName = getBaseName(tarFile.getFileName().toString());
                // 生成目标文件名（添加.log后缀）
                String targetFileName = baseName + ".log";

                // 创建唯一路径
                Path targetPath = getUniquePath(destDir, targetFileName, counter);
                Files.createDirectories(targetPath.getParent());

                // 复制文件内容
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
        // 首次尝试使用原始文件名
        Path original = baseDir.resolve(fileName);
        if (!Files.exists(original)) return original;

        // 拆分文件名和扩展名
        String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
        String extension = fileName.substring(fileName.lastIndexOf('.'));

        // 添加序号直到找到可用文件名
        while (true) {
            String newName = baseName + "_" + counter.getAndIncrement() + extension;
            Path newPath = baseDir.resolve(newName);
            if (!Files.exists(newPath)) return newPath;
        }
    }

    public static void main(String[] args) {
        try {
            extractAllCompressedFiles(FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_ROOT_PATH),FileAccessor.getProjectRootFolderPath(SLOW_INTERFACE_STEP_ONE_PATH));
            System.out.println("Extraction completed successfully!");
        } catch (IOException e) {
            System.err.println("Extraction failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}