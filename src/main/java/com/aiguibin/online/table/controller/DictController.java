package com.aiguibin.online.table.controller;

import com.aiguibin.online.table.entity.DictItem;
import com.aiguibin.online.table.entity.DictType;
import com.aiguibin.online.table.model.ApiResponse;
import com.aiguibin.online.table.service.DictItemService;
import com.aiguibin.online.table.service.DictTypeService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import java.util.List;

/**
 * 字典控制器
 */
@RestController
@RequestMapping("/api/dict")
public class DictController {
    
    @Resource
    private DictTypeService dictTypeService;
    
    @Resource
    private DictItemService dictItemService;
    
    // ==================== 字典类型 ====================
    
    /**
     * 分页查询字典类型
     */
    @GetMapping("/types")
    public ApiResponse<Page<DictType>> listTypes(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String dictTypeCode,
                                                @RequestParam(required = false) String dictTypeName) {
        Page<DictType> pageData = dictTypeService.page(page, size, dictTypeCode, dictTypeName);
        return ApiResponse.success(pageData);
    }
    
    /**
     * 查询所有启用的字典类型
     */
    @GetMapping("/types/enabled")
    public ApiResponse<List<DictType>> listEnabledTypes() {
        List<DictType> types = dictTypeService.listEnabled();
        return ApiResponse.success(types);
    }
    
    /**
     * 根据ID查询字典类型
     */
    @GetMapping("/types/{id}")
    public ApiResponse<DictType> getTypeById(@PathVariable Long id) {
        DictType type = dictTypeService.getById(id);
        return ApiResponse.success(type);
    }
    
    /**
     * 创建字典类型
     */
    @PostMapping("/types")
    public ApiResponse<Long> createType(@RequestBody @Validated DictType dictType, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        Long id = dictTypeService.create(dictType, operator);
        return ApiResponse.success(id);
    }
    
    /**
     * 更新字典类型
     */
    @PutMapping("/types/{id}")
    public ApiResponse<Boolean> updateType(@PathVariable Long id, @RequestBody @Validated DictType dictType, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        boolean ok = dictTypeService.update(id, dictType, operator);
        return ApiResponse.success(ok);
    }
    
    /**
     * 删除字典类型
     */
    @DeleteMapping("/types/{id}")
    public ApiResponse<Boolean> deleteType(@PathVariable Long id, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        boolean ok = dictTypeService.delete(id, operator);
        return ApiResponse.success(ok);
    }
    
    // ==================== 字典码值 ====================
    
    /**
     * 分页查询字典码值
     */
    @GetMapping("/items")
    public ApiResponse<Page<DictItem>> listItems(@RequestParam(defaultValue = "1") int page,
                                                @RequestParam(defaultValue = "10") int size,
                                                @RequestParam(required = false) String dictTypeCode,
                                                @RequestParam(required = false) String dictValue,
                                                @RequestParam(required = false) String dictLabel,
                                                @RequestParam(required = false) String groupCode) {
        Page<DictItem> pageData = dictItemService.page(page, size, dictTypeCode, dictValue, dictLabel, groupCode);
        return ApiResponse.success(pageData);
    }
    
    /**
     * 根据字典类型编码查询字典码值
     */
    @GetMapping("/items/type/{dictTypeCode}")
    public ApiResponse<List<DictItem>> listItemsByType(@PathVariable String dictTypeCode) {
        List<DictItem> items = dictItemService.listByType(dictTypeCode);
        return ApiResponse.success(items);
    }
    
    /**
     * 根据字典类型编码和分组编码查询字典码值
     */
    @GetMapping("/items/type/{dictTypeCode}/group/{groupCode}")
    public ApiResponse<List<DictItem>> listItemsByTypeAndGroup(@PathVariable String dictTypeCode, @PathVariable(required = false) String groupCode) {
        List<DictItem> items = dictItemService.listByTypeAndGroup(dictTypeCode, groupCode);
        return ApiResponse.success(items);
    }
    
    /**
     * 根据ID查询字典码值
     */
    @GetMapping("/items/{id}")
    public ApiResponse<DictItem> getItemById(@PathVariable Long id) {
        DictItem item = dictItemService.getById(id);
        return ApiResponse.success(item);
    }
    
    /**
     * 创建字典码值
     */
    @PostMapping("/items")
    public ApiResponse<Long> createItem(@RequestBody @Validated DictItem dictItem, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        Long id = dictItemService.create(dictItem, operator);
        return ApiResponse.success(id);
    }
    
    /**
     * 更新字典码值
     */
    @PutMapping("/items/{id}")
    public ApiResponse<Boolean> updateItem(@PathVariable Long id, @RequestBody @Validated DictItem dictItem, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        boolean ok = dictItemService.update(id, dictItem, operator);
        return ApiResponse.success(ok);
    }
    
    /**
     * 删除字典码值
     */
    @DeleteMapping("/items/{id}")
    public ApiResponse<Boolean> deleteItem(@PathVariable Long id, HttpServletRequest request) {
        String operator = (String) request.getAttribute("operator");
        boolean ok = dictItemService.delete(id, operator);
        return ApiResponse.success(ok);
    }
}