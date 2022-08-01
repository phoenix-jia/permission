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
@TableName("comms")
@ApiModel(value="Comms对象", description="")
public class Comms implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String commCode;

    private String commName;

    @ApiModelProperty(value = "所属网格名")
    private String netName;

    @ApiModelProperty(value = "所属居委")
    private String jwName;

    private String commAddr;

    private String province;

    private String district;

    private String town;

    private String type;

    private Double lat;

    private Double lng;

    @ApiModelProperty(value = "vpn服务器IP")
    private String lanIp;

    @ApiModelProperty(value = "vpn数据库名")
    private String lanDatabase;

    @ApiModelProperty(value = "vpn本地后端路径")
    private String lanUrl;

    @ApiModelProperty(value = "手机号")
    private String mobile;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE, insertStrategy = FieldStrategy.IGNORED)
    private Date updatedAt;

    private Double area;

    private String city;

    @ApiModelProperty(value = "居民公约分数上限")
    private Integer residentRuleScoreMax;

    @ApiModelProperty(value = "居民公约分数下限")
    private Integer residentRuleScoreMin;

    @ApiModelProperty(value = "居民公约及格分数")
    private Integer residentRuleScorePass;

    @ApiModelProperty(value = "客流墓园子类型")
    private Integer subType;

//    @ApiModelProperty(value = "系数")
//    private Double fix;

}
