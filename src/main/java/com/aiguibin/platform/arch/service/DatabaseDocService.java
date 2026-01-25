package com.aiguibin.platform.arch.service;

import java.io.IOException;
import java.util.List;

public interface DatabaseDocService {
    String generateDoc(String dbConfigId, List<String> tableNames) throws IOException;
    void generateDocForAllDatabases() throws IOException;
}