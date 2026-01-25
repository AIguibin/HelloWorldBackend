package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.service.DbValidationService;
import com.aiguibin.platform.arch.vo.ValidationRequest;
import com.aiguibin.platform.arch.vo.ValidationResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/db/validation")
public class DbValidationController {

    @Autowired
    private DbValidationService validationService;

    /**
     * 执行表结构校验
     * @param request 校验请求参数
     * @return 校验任务ID
     */
    @PostMapping
    public String executeValidation(@RequestBody ValidationRequest request) {
        return validationService.executeValidation(request);
    }

    /**
     * 获取校验任务详情
     * @param id 校验任务ID
     * @return 校验任务详情
     */
    @GetMapping("/{id}")
    public ValidationResult getValidationTask(@PathVariable String id) {
        return validationService.getValidationTask(id);
    }

    /**
     * 获取校验任务列表
     * @return 校验任务列表
     */
    @GetMapping
    public List<ValidationResult> getValidationTasks() {
        return validationService.getValidationTasks();
    }

    /**
     * 获取差异报告
     * @param id 校验任务ID
     * @return 差异报告
     */
    @GetMapping("/{id}/report")
    public byte[] getReport(@PathVariable String id) {
        return validationService.getReport(id);
    }

    /**
     * 获取修复脚本
     * @param id 校验任务ID
     * @return 修复脚本
     */
    @GetMapping("/{id}/fix-script")
    public byte[] getFixScript(@PathVariable String id) {
        return validationService.getFixScript(id);
    }
}
