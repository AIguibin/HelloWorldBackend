package com.aiguibin.platform.arch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 字典项数据DTO
 */
@Data
@ApiModel("字典项数据DTO")
public class DictItemDataDTO {

    @ApiModelProperty(value = "排序号")
    @NotNull(message = "排序号不能为空")
    private Integer dctSeq;

    @ApiModelProperty(value = "分组")
    @Length(max = 100, message = "分组长度不能超过100字符")
    private String dctGrp;

    @ApiModelProperty(value = "字典键", required = true)
    @NotBlank(message = "字典键不能为空")
    @Length(max = 200, message = "字典键长度不能超过200字符")
    private String dctKey;

    @ApiModelProperty(value = "字典值名称", required = true)
    @NotBlank(message = "字典值名称不能为空")
    @Length(max = 500, message = "字典值名称长度不能超过500字符")
    private String dctValNm;

    @ApiModelProperty(value = "字典值", required = true)
    @NotBlank(message = "字典值不能为空")
    @Length(max = 2000, message = "字典值长度不能超过2000字符")
    private String dctVal;

    @ApiModelProperty(value = "字典描述")
    @Length(max = 1000, message = "字典描述长度不能超过1000字符")
    private String dctDsc;

    @ApiModelProperty(value = "状态码")
    @Length(max = 2, message = "状态码长度不能超过2字符")
    private String stcd;
}
