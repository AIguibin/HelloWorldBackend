package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.dto.ApproveDTO;
import com.aiguibin.platform.arch.dto.ChangeQueryDTO;
import com.aiguibin.platform.arch.dto.DictChangeApplyDTO;
import com.aiguibin.platform.arch.service.DictChangeApplyService;
import com.aiguibin.platform.arch.service.DictChangeApproveService;
import com.aiguibin.platform.arch.service.DictChangeExecuteService;
import com.aiguibin.platform.arch.service.DictChangeQueryService;
import com.aiguibin.platform.arch.dto.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 变更管理控制器
 */
@RestController
@RequestMapping("/api/dict/change")
@Api(tags = "数据字典变更管理")
@Slf4j
@Validated
public class DictChangeController {

    @Autowired
    private DictChangeApplyService applyService;

    @Autowired
    private DictChangeApproveService approveService;

    @Autowired
    private DictChangeExecuteService executeService;

    @Autowired
    private DictChangeQueryService queryService;

    @PostMapping("/apply")
    @ApiOperation("提交变更申请")
    public Result<DictTypeChangeVO> applyChange(@Valid @RequestBody DictChangeApplyDTO dto) {
        log.info("提交变更申请: {}", dto);
        DictTypeChangeVO result = applyService.applyChange(dto);
        return Result.success("提交成功", result);
    }

    @PostMapping("/draft")
    @ApiOperation("保存草稿")
    public Result<DictTypeChangeVO> saveDraft(@Valid @RequestBody DictChangeApplyDTO dto) {
        // #region agent log
        try {
            ObjectMapper mapper = new ObjectMapper();
            String logEntry = mapper.writeValueAsString(new java.util.HashMap<String, Object>() {{
                put("id", "log_" + System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString().substring(0, 8));
                put("timestamp", System.currentTimeMillis());
                put("location", "DictChangeController.java:55");
                put("message", "保存草稿请求入口");
                put("data", new java.util.HashMap<String, Object>() {{
                    put("changeType", dto.getChangeType() != null ? dto.getChangeType().toString() : null);
                    put("itemChangesCount", dto.getItemChanges() != null ? dto.getItemChanges().size() : 0);
                    if (dto.getItemChanges() != null && !dto.getItemChanges().isEmpty()) {
                        java.util.List<java.util.Map<String, Object>> items = new java.util.ArrayList<>();
                        for (int i = 0; i < dto.getItemChanges().size(); i++) {
                            com.aiguibin.platform.arch.dto.DictItemChangeDTO item = dto.getItemChanges().get(i);
                            java.util.Map<String, Object> itemData = new java.util.HashMap<>();
                            itemData.put("index", i);
                            itemData.put("changeOperation", item.getChangeOperation());
                            if (item.getNewData() != null) {
                                itemData.put("newData_dctSeq", item.getNewData().getDctSeq());
                                itemData.put("newData_dctKey", item.getNewData().getDctKey());
                            }
                            items.add(itemData);
                        }
                        put("itemChanges", items);
                    }
                }});
                put("sessionId", "debug-session");
                put("runId", "run1");
                put("hypothesisId", "A");
            }});
            Files.write(Paths.get("e:\\WorkSpace\\HelloWorldBackend\\aiguibin-platform-arch\\.cursor\\debug.log"), 
                (logEntry + "\n").getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (Exception e) {}
        // #endregion
        log.info("保存草稿: {}", dto);
        DictTypeChangeVO result = applyService.saveDraft(dto);
        return Result.success("保存成功", result);
    }

    @PostMapping("/approve/{changeId}")
    @ApiOperation("审批变更")
    public Result<Void> approveChange(
            @PathVariable String changeId,
            @Valid @RequestBody ApproveDTO dto) {
        log.info("审批变更: changeId={}, approveResult={}", changeId, dto.getApproveResult());
        approveService.approveChange(changeId, dto);
        return Result.success("审批成功");
    }

    @PostMapping("/revoke/{changeId}")
    @ApiOperation("撤回审批")
    public Result<Void> revokeApprove(@PathVariable String changeId) {
        log.info("撤回审批: changeId={}", changeId);
        approveService.revokeApprove(changeId);
        return Result.success("撤回成功");
    }

    @PostMapping("/execute/{changeId}")
    @ApiOperation("执行变更")
    public Result<ExecuteResultVO> executeChange(@PathVariable String changeId) {
        log.info("执行变更: changeId={}", changeId);
        ExecuteResultVO result = executeService.executeChange(changeId);
        return Result.success("执行成功", result);
    }

    @PostMapping("/cancel/{changeId}")
    @ApiOperation("取消执行")
    public Result<Void> cancelExecute(@PathVariable String changeId) {
        log.info("取消执行: changeId={}", changeId);
        executeService.cancelExecute(changeId);
        return Result.success("取消成功");
    }

    @GetMapping("/detail/{changeId}")
    @ApiOperation("获取变更详情")
    public Result<ChangeDetailVO> getChangeDetail(@PathVariable String changeId) {
        log.info("获取变更详情: changeId={}", changeId);
        ChangeDetailVO detail = queryService.getChangeDetail(changeId);
        return Result.success(detail);
    }

    @GetMapping("/list")
    @ApiOperation("查询变更记录")
    public Result<PageResult<DictChangeVO>> queryChanges(ChangeQueryDTO queryDTO) {
        log.info("查询变更记录: {}", queryDTO);
        PageResult<DictChangeVO> result = queryService.queryChanges(queryDTO);
        return Result.success(result);
    }

    @GetMapping("/pending/approve")
    @ApiOperation("获取待审批列表")
    public Result<List<DictChangeVO>> getPendingApprove() {
        log.info("获取待审批列表");
        List<DictChangeVO> result = queryService.getPendingApprove();
        return Result.success(result);
    }

    @GetMapping("/pending/execute")
    @ApiOperation("获取待执行列表")
    public Result<List<DictChangeVO>> getPendingExecute() {
        log.info("获取待执行列表");
        List<DictChangeVO> result = queryService.getPendingExecute();
        return Result.success(result);
    }

    @PostMapping("/items/import")
    @ApiOperation("导入字典项")
    public Result<DictItemImportVO> importDictItems(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dictType", required = false) String dictType) {
        log.info("导入字典项: dictType={}", dictType);
        DictItemImportVO result = applyService.importDictItems(file, dictType);
        return Result.success("导入成功", result);
    }

    @GetMapping("/export")
    @ApiOperation("导出变更记录")
    public void exportChanges(ChangeQueryDTO queryDTO, HttpServletResponse response) {
        log.info("导出变更记录: {}", queryDTO);
        queryService.exportChanges(queryDTO, response);
    }

    @GetMapping("/dict-types")
    @ApiOperation("获取字典类型列表")
    public Result<List<DictTypeVO>> getDictTypes() {
        log.info("获取字典类型列表");
        List<DictTypeVO> dictTypes = queryService.getDictTypes();
        return Result.success(dictTypes);
    }

    @GetMapping("/load-dict-type/{dictTypeId}")
    @ApiOperation("加载字典类型详情")
    public Result<DictTypeDetailVO> loadDictType(@PathVariable String dictTypeId) {
        log.info("加载字典类型详情: dictTypeId={}", dictTypeId);
        DictTypeDetailVO dictTypeDetail = queryService.loadDictType(dictTypeId);
        return Result.success(dictTypeDetail);
    }
}
