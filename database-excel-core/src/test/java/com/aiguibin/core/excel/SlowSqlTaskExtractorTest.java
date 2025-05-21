package com.aiguibin.core.excel;

import com.aiguibin.core.common.FileAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SlowSqlTaskExtractorTest {

    @Mock
    private Log mockLogger;

    @TempDir
    Path tempDir;

    @Test
    void testTabbleListIsExist_DirectoryNotExists_CreatesDirectory() throws IOException {
        // 模拟静态方法
        try (MockedStatic<FileAccessor> mockedFileAccessor = mockStatic(FileAccessor.class);
             MockedStatic<LogFactory> mockedLogFactory = mockStatic(LogFactory.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            // 配置静态方法返回值
            Path expectedPath = tempDir.resolve("database-excel-core/docs/tableList");
            mockedFileAccessor.when(() -> FileAccessor.getProjectRootFolderPath(anyString()))
                    .thenReturn(expectedPath);
            mockedLogFactory.when(() -> LogFactory.getLog(FileAccessor.class))
                    .thenReturn(mockLogger);
            mockedFiles.when(() -> Files.notExists(expectedPath)).thenReturn(true);

            // 执行测试方法
            SlowSqlTaskExtractor extractor = new SlowSqlTaskExtractor();
            extractor.createTaskBySlowSQLAnalyzer();

            // 验证目录创建和日志
            mockedFiles.verify(() -> Files.createDirectories(expectedPath));
            verify(mockLogger).info("目录已创建: database-excel-core/docs/tableList");
            verify(mockLogger).info(expectedPath.toString());
        }
    }

    @Test
    void testTabbleListIsExist_DirectoryExists_NoCreation() {
        try (MockedStatic<FileAccessor> mockedFileAccessor = mockStatic(FileAccessor.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            Path existingPath = tempDir.resolve("existing-dir");
            mockedFileAccessor.when(() -> FileAccessor.getProjectRootFolderPath(anyString()))
                    .thenReturn(existingPath);
            mockedFiles.when(() -> Files.notExists(existingPath)).thenReturn(false);

            SlowSqlTaskExtractor extractor = new SlowSqlTaskExtractor();
            extractor.createTaskBySlowSQLAnalyzer();

            mockedFiles.verify(() -> Files.createDirectories(existingPath), never());
            verify(mockLogger, never()).info("目录已创建: database-excel-core/docs/tableList");
        }
    }

    @Test
    void testTabbleListIsExist_CreateDirectoryFails_LogsError() throws IOException {
        try (MockedStatic<FileAccessor> mockedFileAccessor = mockStatic(FileAccessor.class);
             MockedStatic<LogFactory> mockedLogFactory = mockStatic(LogFactory.class);
             MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {

            Path targetPath = tempDir.resolve("error-dir");
            mockedFileAccessor.when(() -> FileAccessor.getProjectRootFolderPath(anyString()))
                    .thenReturn(targetPath);
            mockedLogFactory.when(() -> LogFactory.getLog(FileAccessor.class))
                    .thenReturn(mockLogger);
            mockedFiles.when(() -> Files.notExists(targetPath)).thenReturn(true);
            mockedFiles.when(() -> Files.createDirectories(targetPath))
                    .thenThrow(new IOException("模拟异常"));

            SlowSqlTaskExtractor extractor = new SlowSqlTaskExtractor();
            extractor.createTaskBySlowSQLAnalyzer();

            verify(mockLogger).info("目录创建失败：\n\r", any(IOException.class));
        }
    }
}