package com.aiguibin.core.excel;

import com.aiguibin.core.common.FileAccessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;

class SlowSqlTaskExtractorTest {

    @TempDir
    Path tempDir; // 使用JUnit 5的临时目录功能

    @Test
    void testTabbleListIsExist_ReturnsExpectedPath() {
        // 模拟静态方法FileAccessor.getProjectRootFolderPath
        try (MockedStatic<FileAccessor> mockedFileAccessor = Mockito.mockStatic(FileAccessor.class)) {
            // 定义预期路径
            String relativePath = "database-excel-core/docs/tableList";
            Path expectedPath = tempDir.resolve(relativePath);

            // 当调用静态方法时返回模拟路径
            mockedFileAccessor.when(() -> FileAccessor.getProjectRootFolderPath(relativePath))
                    .thenReturn(expectedPath);

            // 调用被测方法
            SlowSqlTaskExtractor extractor = new SlowSqlTaskExtractor();
            Path result = extractor.tabbleListIsExist();

            // 验证路径是否符合预期
            assertEquals(expectedPath, result, "返回的路径应与预期一致");
        }
    }

    @Test
    void testTabbleListIsExist_HandlesNullPath() {
        try (MockedStatic<FileAccessor> mockedFileAccessor = Mockito.mockStatic(FileAccessor.class)) {
            // 模拟返回null路径
            mockedFileAccessor.when(() -> FileAccessor.getProjectRootFolderPath(anyString()))
                    .thenReturn(null);

            SlowSqlTaskExtractor extractor = new SlowSqlTaskExtractor();
            Path result = extractor.tabbleListIsExist();

            assertNull(result, "当FileAccessor返回null时，方法应返回null");
        }
    }
}