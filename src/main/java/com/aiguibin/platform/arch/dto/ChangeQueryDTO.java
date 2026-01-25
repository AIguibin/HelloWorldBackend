package com.aiguibin.platform.arch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 变更查询DTO
 */
@Data
@ApiModel("变更查询DTO")
public class ChangeQueryDTO {

    @ApiModelProperty(value = "页码", example = "1")
    private Integer page = 1;

    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer size = 10;

    @ApiModelProperty(value = "变更单号")
    @Length(max = 50, message = "变更单号长度不能超过50字符")
    private String changeNo;

    @ApiModelProperty(value = "字典类型")
    @Length(max = 100, message = "字典类型长度不能超过100字符")
    private String dictType;

    @ApiModelProperty(value = "变更类型")
    private String changeType;

    @ApiModelProperty(value = "审批状态")
    private String approveStatus;

    @ApiModelProperty(value = "执行状态")
    private String executeStatus;

    @ApiModelProperty(value = "申请人")
    @Length(max = 50, message = "申请人长度不能超过50字符")
    private String applyUser;

    @ApiModelProperty(value = "申请开始时间")
    private Date startApplyTime;

    @ApiModelProperty(value = "申请结束时间")
    private Date endApplyTime;

    @ApiModelProperty(value = "排序字段")
    private String orderBy;

    @ApiModelProperty(value = "排序方向")
    private String orderDirection;
}
