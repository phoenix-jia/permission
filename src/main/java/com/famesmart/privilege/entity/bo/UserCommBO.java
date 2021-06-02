package com.famesmart.privilege.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value = "用户社区BO", description = "用户社区")
public class UserCommBO {

    @NotNull
    @ApiModelProperty(value = "用户ID", name = "id", example = "1", required = true)
    private Integer userId;

    @NotNull
    @ApiModelProperty(value = "社区代码列表", name = "commCodeList", required = true)
    private List<String> commCodes;
}
