package com.aiguibin.platform.arch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * 字典类型变更DTO
 */
@Data
@ApiModel("字典类型变更DTO")
public class DictTypeChangeDTO {

    @ApiModelProperty(value = "原始字典类型编码")
    private String oldDctTp;

    @ApiModelProperty(value = "新字典类型编码")
    @NotBlank(message = "字典类型编码不能为空")
    @Length(max = 100, message = "字典类型编码长度不能超过100字符")
    private String newDctTp;

    @ApiModelProperty(value = "原始字典类型名称")
    private String oldDctTpNm;

    @ApiModelProperty(value = "新字典类型名称")
    @NotBlank(message = "字典类型名称不能为空")
    @Length(max = 200, message = "字典类型名称长度不能超过200字符")
    private String newDctTpNm;

    @ApiModelProperty(value = "原始字典类型描述")
    private String oldDctTpDsc;

    @ApiModelProperty(value = "新字典类型描述")
    @Length(max = 500, message = "字典类型描述长度不能超过500字符")
    private String newDctTpDsc;
}
