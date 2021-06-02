package com.famesmart.privilege.entity.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "用户BO", description = "用户数据")
public class UserBO {

    @NotBlank
    @ApiModelProperty(value = "用户名", name = "username", required = true)
    private String username;

    @NotBlank
    @ApiModelProperty(value = "密码", name = "password", required = true)
    private String password;

    @ApiModelProperty(value = "姓名", name = "name", required = false)
    private String name;

    @ApiModelProperty(value = "联系电话", name = "phone", required = false)
    private String phone;

    @ApiModelProperty(value = "职位类型", name = "position", required = false)
    private String position;

    @NotNull
    @ApiModelProperty(value = "平台:0-全部,1-PC,2-小程序", name = "platform", example = "0", required = true)
    private Integer platform;

    @ApiModelProperty(value = "过期时间,yyyy-MM-dd", name = "expireAt", required = false)
    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd")
    private Date expireAt;

    @ApiModelProperty(value = "居民ID", name = "residentId", example = "1", required = false)
    private Integer residentId;

    @ApiModelProperty(value = "行政区划级", name = "areaLevel", required = false)
    private String areaLevel;

    @ApiModelProperty(value = "主页类型", name = "homePageType", required = false)
    private String homePageType;

    @ApiModelProperty(value = "角色ID列表", name = "role", required = false)
    private List<Integer> roleIds;

    @ApiModelProperty(value = "权限ID列表", name = "privilege", required = false)
    private List<Integer> privilegeIds;

    @ApiModelProperty(value = "社区代码", name = "comm", required = false)
    private String commCode;
}
