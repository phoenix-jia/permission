package com.famesmart.privilege.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value = "用户社区预警BO", description = "用户社区预警")
public class UserCommAlarmBO {


    @ApiModelProperty(value = "用户ID", name = "id", example = "1", required = true)
    @NotNull
    private Integer userId;

    @ApiModelProperty(value = "社区预警列表", name = "commAlarmIdList", required = true)
    @NotNull
    private List<Integer> commAlarmIds;
}
