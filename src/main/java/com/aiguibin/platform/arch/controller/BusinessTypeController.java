package com.aiguibin.platform.arch.controller;

import com.aiguibin.platform.arch.entity.BusinessType;
import com.aiguibin.platform.arch.dto.ResultVO;
import com.aiguibin.platform.arch.service.BusinessTypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 业务类型控制器
 * 路径前缀：/api/business-types
 */
@RestController
@RequestMapping("/api/business-types")
public class BusinessTypeController {

    @Resource
    private BusinessTypeService businessTypeService;

    /**
     * 查询所有业务类型
     * @return ResultVO<List<BusinessType>> 业务类型列表
     */
    @GetMapping
    public ResultVO<List<BusinessType>> getAllBusinessTypes() {
        List<BusinessType> businessTypes = businessTypeService.getAllBusinessTypes();
        return ResultVO.success(businessTypes);
    }

    /**
     * 根据业务类型编码查询业务类型
     * @param code 业务类型编码
     * @return ResultVO<BusinessType> 业务类型对象
     */
    @GetMapping("/{code}")
    public ResultVO<BusinessType> getBusinessTypeByCode(@PathVariable String code) {
        BusinessType businessType = businessTypeService.getBusinessTypeByCode(code);
        return businessType != null ? ResultVO.success(businessType) : ResultVO.error("业务类型不存在");
    }
}