package com.aiguibin.platform.arch.service;

import com.aiguibin.platform.arch.vo.ValidationRequest;
import com.aiguibin.platform.arch.vo.ValidationResult;

import java.util.List;

public interface DbValidationService {
    /**
     * 执行表结构校验
     * @param request 校验请求参数
     * @return 校验任务ID
     */
    String executeValidation(ValidationRequest request);

    /**
     * 获取校验任务详情
     * @param id 校验任务ID
     * @return 校验任务详情
     */
    ValidationResult getValidationTask(String id);

    /**
     * 获取校验任务列表
     * @return 校验任务列表
     */
    List<ValidationResult> getValidationTasks();

    /**
     * 获取差异报告
     * @param id 校验任务ID
     * @return 差异报告
     */
    byte[] getReport(String id);

    /**
     * 获取修复脚本
     * @param id 校验任务ID
     * @return 修复脚本
     */
    byte[] getFixScript(String id);

    /**
     * 删除校验任务
     * @param id 校验任务ID
     */
    void deleteValidationTask(String id);
}
