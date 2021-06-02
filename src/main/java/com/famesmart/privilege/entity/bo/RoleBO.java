package com.famesmart.privilege.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@ApiModel(value = "角色BO", description = "角色信息")
public class RoleBO {

    @NotBlank
    @ApiModelProperty(value = "角色名称", name = "name", required = true)
    private String name;

    @ApiModelProperty(value = "权限id列表", name = "privilegeIdList", required = true)
    private List<Integer> privilegeIds;
}
