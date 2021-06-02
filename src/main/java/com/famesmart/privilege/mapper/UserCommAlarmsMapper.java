package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.UserCommAlarms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.famesmart.privilege.entity.vo.CommAlarmVO;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
public interface UserCommAlarmsMapper extends BaseMapper<UserCommAlarms> {

    List<CommAlarmVO> selectCommAlarmByUserId(Integer userId);
}
