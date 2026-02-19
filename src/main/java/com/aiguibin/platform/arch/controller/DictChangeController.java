package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.dto.ApproveDTO;
import com.aiguibin.platform.arch.dto.ChangeQueryDTO;
import com.aiguibin.platform.arch.dto.DictChangeApplyDTO;
import com.aiguibin.platform.arch.dto.ResultVO;
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
    public ResultVO<DictTypeChangeVO> applyChange(@Valid @RequestBody DictChangeApplyDTO dto) {
        log.info("提交变更申请: {}", dto);
        DictTypeChangeVO result = applyService.applyChange(dto);
        return ResultVO.success("提交成功", result);
    }

    @PostMapping("/draft")
    @ApiOperation("保存草稿")
    public ResultVO<DictTypeChangeVO> saveDraft(@Valid @RequestBody DictChangeApplyDTO dto) {
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
        return ResultVO.success("保存成功", result);
    }

    @PostMapping("/approve/{changeId}")
    @ApiOperation("审批变更")
    public ResultVO<Void> approveChange(
            @PathVariable String changeId,
            @Valid @RequestBody ApproveDTO dto) {
        log.info("审批变更: changeId={}, approveResult={}", changeId, dto.getApproveResult());
        approveService.approveChange(changeId, dto);
        return ResultVO.success();
    }

    @PostMapping("/revoke/{changeId}")
    @ApiOperation("撤回审批")
    public ResultVO<Void> revokeApprove(@PathVariable String changeId) {
        log.info("撤回审批: changeId={}", changeId);
        approveService.revokeApprove(changeId);
        return ResultVO.success();
    }

    @PostMapping("/execute/{changeId}")
    @ApiOperation("执行变更")
    public ResultVO<ExecuteResultVO> executeChange(@PathVariable String changeId) {
        log.info("执行变更: changeId={}", changeId);
        ExecuteResultVO result = executeService.executeChange(changeId);
        return ResultVO.success("执行成功", result);
    }

    @PostMapping("/cancel/{changeId}")
    @ApiOperation("取消执行")
    public ResultVO<Void> cancelExecute(@PathVariable String changeId) {
        log.info("取消执行: changeId={}", changeId);
        executeService.cancelExecute(changeId);
        return ResultVO.success();
    }

    @GetMapping("/detail/{changeId}")
    @ApiOperation("获取变更详情")
    public ResultVO<ChangeDetailVO> getChangeDetail(@PathVariable String changeId) {
        log.info("获取变更详情: changeId={}", changeId);
        ChangeDetailVO detail = queryService.getChangeDetail(changeId);
        return ResultVO.success(detail);
    }

    @GetMapping("/list")
    @ApiOperation("查询变更记录")
    public ResultVO<PageResultVO<DictChangeVO>> queryChanges(ChangeQueryDTO queryDTO) {
        log.info("查询变更记录: {}", queryDTO);
        PageResultVO<DictChangeVO> result = queryService.queryChanges(queryDTO);
        return ResultVO.success(result);
    }

    @GetMapping("/pending/approve")
    @ApiOperation("获取待审批列表")
    public ResultVO<List<DictChangeVO>> getPendingApprove() {
        log.info("获取待审批列表");
        List<DictChangeVO> result = queryService.getPendingApprove();
        return ResultVO.success(result);
    }

    @GetMapping("/pending/execute")
    @ApiOperation("获取待执行列表")
    public ResultVO<List<DictChangeVO>> getPendingExecute() {
        log.info("获取待执行列表");
        List<DictChangeVO> result = queryService.getPendingExecute();
        return ResultVO.success(result);
    }

    @PostMapping("/items/import")
    @ApiOperation("导入字典项")
    public ResultVO<DictItemImportVO> importDictItems(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "dictType", required = false) String dictType) {
        log.info("导入字典项: dictType={}", dictType);
        DictItemImportVO result = applyService.importDictItems(file, dictType);
        return ResultVO.success("导入成功", result);
    }

    @GetMapping("/export")
    @ApiOperation("导出变更记录")
    public void exportChanges(ChangeQueryDTO queryDTO, HttpServletResponse response) {
        log.info("导出变更记录: {}", queryDTO);
        queryService.exportChanges(queryDTO, response);
    }

    @GetMapping("/dict-types")
    @ApiOperation("获取字典类型列表")
    public ResultVO<List<DictTypeVO>> getDictTypes() {
        log.info("获取字典类型列表");
        List<DictTypeVO> dictTypes = queryService.getDictTypes();
        return ResultVO.success(dictTypes);
    }

    @GetMapping("/load-dict-type/{dictTypeId}")
    @ApiOperation("加载字典类型详情")
    public ResultVO<DictTypeDetailVO> loadDictType(@PathVariable String dictTypeId) {
        log.info("加载字典类型详情: dictTypeId={}", dictTypeId);
        DictTypeDetailVO dictTypeDetail = queryService.loadDictType(dictTypeId);
        return ResultVO.success(dictTypeDetail);
    }
}