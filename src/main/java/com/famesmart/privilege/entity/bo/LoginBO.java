package com.famesmart.privilege.entity.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "登录对象BO", description = "登录数据")
public class LoginBO {

    @NotBlank
    @ApiModelProperty(value = "用户名", name = "username", required = true)
    private String username;

    @NotBlank
    @ApiModelProperty(value = "密码", name = "password", required = true)
    private String password;
}
