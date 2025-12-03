package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.ChangeHistory;
import com.aiguibin.platform.arch.entity.ChangeRecord;
import com.aiguibin.platform.arch.model.ApiResponse;
import com.aiguibin.platform.arch.service.ChangeRecordService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/change-records")
public class CodeScriptChangeRecordController {

    @Resource
    private ChangeRecordService service;

    @GetMapping
    public ApiResponse<Page<ChangeRecord>> list(@RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "10") int size,
                                                          @RequestParam(required = false) String groupName,
                                                          @RequestParam(required = false) String developerNum,
                                                          @RequestParam(required = false) String developType,
                                                          @RequestParam(required = false) String currentStatus,
                                                          @RequestParam(required = false) String serviceName,
                                                          @RequestParam(required = false) String defectNumber,
                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        Page<ChangeRecord> pageData = service.page(page, size, groupName, developerNum, developType, currentStatus, serviceName, defectNumber, startTime, endTime);
        return ApiResponse.success(pageData);
    }

    @GetMapping("/{id}")
    public ApiResponse<ChangeRecord> detail(@PathVariable Long id) {
        return ApiResponse.success(service.getById(id));
    }

    @PostMapping
    public ApiResponse<Long> create(@RequestBody @Validated ChangeRecord r, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        String pagePath = decodeHeader(request.getHeader("X-Page-Path"));
        String buttonName = decodeHeader(request.getHeader("X-Button-Name"));
        String ip = resolveIp(request);
        Long id = service.create(r, operator, pagePath, buttonName, ip);
        return ApiResponse.success(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<Boolean> update(@PathVariable Long id, @RequestBody @Validated ChangeRecord r, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        String pagePath = decodeHeader(request.getHeader("X-Page-Path"));
        String buttonName = decodeHeader(request.getHeader("X-Button-Name"));
        String ip = resolveIp(request);
        boolean ok = service.update(id, r, operator, pagePath, buttonName, ip);
        return ApiResponse.success(ok);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        String pagePath = decodeHeader(request.getHeader("X-Page-Path"));
        String buttonName = decodeHeader(request.getHeader("X-Button-Name"));
        String ip = resolveIp(request);
        boolean ok = service.logicalDelete(id, operator, pagePath, buttonName, ip);
        return ApiResponse.success(ok);
    }

    @GetMapping("/{id}/history")
    public ApiResponse<Page<ChangeHistory>> history(@PathVariable Long id,
                                                              @RequestParam(defaultValue = "1") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Page<ChangeHistory> pageData = service.history(id, page, size);
        return ApiResponse.success(pageData);
    }

    private String decodeHeader(String s) {
        if (s == null) return null;
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return s;
        }
    }

    private String resolveIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isEmpty()) return xf.split(",")[0].trim();
        return request.getRemoteAddr();
    }

}