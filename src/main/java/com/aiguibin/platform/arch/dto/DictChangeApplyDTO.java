package com.aiguibin.platform.arch.dto;

import com.aiguibin.platform.arch.enums.ChangeType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 变更申请DTO
 */
@Data
@ApiModel("变更申请DTO")
public class DictChangeApplyDTO {

    @ApiModelProperty(value = "变更类型", required = true)
    @NotNull(message = "变更类型不能为空")
    private ChangeType changeType;

    @ApiModelProperty(value = "字典类型ID", notes = "修改/删除时必填")
    private String dictTypeId;

    @ApiModelProperty(value = "字典类型变更信息")
    @Valid
    private DictTypeChangeDTO typeChange;

    @ApiModelProperty(value = "字典项变更列表")
    @Valid
    @Size(max = 1000, message = "字典项变更数量不能超过1000")
    private List<DictItemChangeDTO> itemChanges;

    @ApiModelProperty(value = "变更原因", required = true)
    @NotBlank(message = "变更原因不能为空")
    @Length(max = 500, message = "变更原因长度不能超过500字符")
    private String changeReason;

    @ApiModelProperty(value = "变更影响")
    @Length(max = 1000, message = "变更影响长度不能超过1000字符")
    private String changeImpact;
}
