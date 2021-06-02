package com.famesmart.privilege.entity.vo;

import lombok.Data;

@Data
public class LoginTimesVO {

    private String clientIp;

    private Integer loginTimes;
}
