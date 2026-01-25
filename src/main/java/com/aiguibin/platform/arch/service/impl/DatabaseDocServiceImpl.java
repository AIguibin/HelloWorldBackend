package com.aiguibin.platform.arch.service.impl;

import com.aiguibin.platform.arch.excel.DatabaseDocGenerator;
import com.aiguibin.platform.arch.service.DatabaseDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DatabaseDocServiceImpl implements DatabaseDocService {
    @Autowired
    private DatabaseDocGenerator databaseDocGenerator;

    @Override
    public String generateDoc(String dbConfigId, List<String> tableNames) throws IOException {
        return databaseDocGenerator.generateDoc(dbConfigId, tableNames);
    }

    @Override
    public void generateDocForAllDatabases() throws IOException {
        databaseDocGenerator.generateDocForAllDatabases();
    }
}