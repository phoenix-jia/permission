package com.famesmart.privilege.entity;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * <p>
 * 
 * </p>
 *
 * @author Jiaxu.Li
 * @since 2021-05-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("v2_saas_users")
@ApiModel(value="Users对象", description="")
public class Users implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String username;

    @JsonIgnore
    private String password;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "职位类型")
    private String position;

    @ApiModelProperty(value = "平台:0-全部,1-PC,2-小程序")
    private Integer platform;

    @ApiModelProperty(value = "过期时间")
    private Date expireAt;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE, insertStrategy = FieldStrategy.IGNORED)
    private Date updatedAt;

    @TableLogic
    private Date deletedAt;

    private Integer residentId;

    @ApiModelProperty(value = "行政区划级别")
    private String areaLevel;

    private String homePageType;

}
