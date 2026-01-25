package com.aiguibin.platform.arch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * 字典项变更DTO
 */
@Data
@ApiModel("字典项变更DTO")
public class DictItemChangeDTO {

    @ApiModelProperty(value = "操作类型", required = true)
    @NotNull(message = "操作类型不能为空")
    private String changeOperation;

    @ApiModelProperty(value = "字典项ID", notes = "修改/删除时必填")
    private String dictId;

    @ApiModelProperty(value = "原始数据", notes = "修改时可选，删除时自动加载")
    private DictItemDataDTO oldData;

    @ApiModelProperty(value = "新数据", notes = "新增/修改时必填")
    @Valid
    private DictItemDataDTO newData;
}
