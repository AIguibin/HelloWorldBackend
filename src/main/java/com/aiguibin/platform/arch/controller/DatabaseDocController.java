package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.service.DatabaseDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/db/doc")
public class DatabaseDocController {
    @Autowired
    private DatabaseDocService databaseDocService;

    @PostMapping
    public String generateDoc(@RequestParam String dbConfigId, @RequestParam(required = false) List<String> tableNames) throws IOException {
        return databaseDocService.generateDoc(dbConfigId, tableNames);
    }

    @PostMapping("/all")
    public void generateDocForAllDatabases() throws IOException {
        databaseDocService.generateDocForAllDatabases();
    }
}