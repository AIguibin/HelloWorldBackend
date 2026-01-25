package com.aiguibin.platform.arch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 审批DTO
 */
@Data
@ApiModel("审批DTO")
public class ApproveDTO {

    @ApiModelProperty(value = "审批结果", required = true)
    @NotNull(message = "审批结果不能为空")
    private Boolean approveResult;

    @ApiModelProperty(value = "审批意见", required = true)
    @NotBlank(message = "审批意见不能为空")
    @Length(max = 500, message = "审批意见长度不能超过500字符")
    private String approveRemark;
}
