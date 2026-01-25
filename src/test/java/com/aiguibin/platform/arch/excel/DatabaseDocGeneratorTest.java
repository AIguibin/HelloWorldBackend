package com.aiguibin.platform.arch.excel;

import com.aiguibin.platform.arch.vo.DbConfig;
import com.aiguibin.platform.arch.service.DbConfigService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DatabaseDocGeneratorTest {
    @Mock
    private DbConfigService dbConfigService;

    @Test
    public void testGenerateDoc() throws IOException {
        DatabaseDocGenerator generator = new DatabaseDocGenerator();
        
        DbConfig dbConfig = new DbConfig();
        dbConfig.setId("1");
        dbConfig.setDatabase("test_db");
        dbConfig.setHost("localhost");
        dbConfig.setPort("3306");
        dbConfig.setUsername("root");
        dbConfig.setPassword("password");
        dbConfig.setDbType("mysql");
        
        Mockito.when(dbConfigService.getDbConfig("1")).thenReturn(dbConfig);
        
        List<String> tableNames = new ArrayList<>();
        tableNames.add("test_table");
        
        String result = generator.generateDoc("1", tableNames);
        System.out.println("生成的文档路径：" + result);
        assert result != null;
    }
}