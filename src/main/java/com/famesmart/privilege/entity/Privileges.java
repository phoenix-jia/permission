package com.famesmart.privilege.entity;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;
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
@TableName("v2_saas_privileges")
@ApiModel(value="Privileges对象", description="")
public class Privileges implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String resource;

    private String operation;

    private String resourceZh;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE, insertStrategy = FieldStrategy.IGNORED)
    private Date updatedAt;

    @TableLogic
    private Date deletedAt;

}
