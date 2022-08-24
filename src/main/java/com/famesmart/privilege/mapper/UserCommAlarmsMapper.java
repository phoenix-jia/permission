package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.UserCommAlarms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.famesmart.privilege.entity.vo.CommAlarmVO;

import java.util.List;

public interface UserCommAlarmsMapper extends BaseMapper<UserCommAlarms> {

    List<CommAlarmVO> selectCommAlarmByUserId(Integer userId);
}
