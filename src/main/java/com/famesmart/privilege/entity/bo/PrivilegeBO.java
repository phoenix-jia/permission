package com.famesmart.privilege.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "权限BO", description = "权限信息")
public class PrivilegeBO {

    @NotBlank
    @ApiModelProperty(value = "资源", name = "resource", required = true)
    private String resource;

    @NotBlank
    @ApiModelProperty(value = "操作", name = "operation", required = true)
    private String operation;

    @ApiModelProperty(value = "资源中文名称", name = "resourceZh", required = true)
    private String resourceZh;
}
