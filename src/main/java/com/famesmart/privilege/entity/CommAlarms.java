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
 * @since 2021-05-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("comm_alarms")
@ApiModel(value="Alarms对象", description="")
public class CommAlarms implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "报警大类")
    private String alarmType;

    @ApiModelProperty(value = "报警内容")
    private String alarmTitle;

    @ApiModelProperty(value = "报警条件")
    private String alarmCondition;

    @ApiModelProperty(value = "停止条件")
    private String stopCondition;

    @ApiModelProperty(value = "级别,1为最高")
    private Integer alarmLevel;

    @ApiModelProperty(value = "检测时间")
    private String checkTime;

    @ApiModelProperty(value = "备注")
    private String note;

    @ApiModelProperty(value = "是否启用")
    private Integer enable;

    @ApiModelProperty(value = "报警过滤频率")
    private Integer frequencyMins;

    @ApiModelProperty(value = "强制新增报警")
    private Integer isForceAdd;

    @ApiModelProperty(value = "需要先审核")
    private Integer isCheckFirst;

    @ApiModelProperty(value = "报警截取视频")
    private Integer isStoreVideo;

    @ApiModelProperty(value = "ws推送")
    private Integer websocketPush;

    @ApiModelProperty(value = "强制弹出视频框")
    private Integer forceVideoPush;

    @ApiModelProperty(value = "是否同步到云端")
    private Integer cloudPush;

    @ApiModelProperty(value = "是否有实时视频")
    private String hasRealtimeVideo;

    @ApiModelProperty(value = "所属社区")
    private String commCode;

    @ApiModelProperty(value = "所属报警规则")
    private Integer alarmRuleId;

    @ApiModelProperty(value = "允许催办的次数")
    private Integer maxUrgeCount;

    @ApiModelProperty(value = "每日最多预警数")
    private Integer maxAlarms;

    @ApiModelProperty(value = "作用时间段")
    private String useTime;

    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE, insertStrategy = FieldStrategy.IGNORED)
    private Date updatedAt;

    private Integer userId;

    @ApiModelProperty(value = "是否为居民公约预警")
    private Integer residentRuleAlarm;

    @ApiModelProperty(value = "居民公约启用")
    private Integer residentRuleEnable;

    @ApiModelProperty(value = "居民公约分数")
    private Integer residentRuleScore;

}
