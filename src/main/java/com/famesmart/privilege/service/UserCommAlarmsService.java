package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.UserCommAlarms;
import com.famesmart.privilege.entity.UserRoles;
import com.famesmart.privilege.entity.vo.CommAlarmVO;
import com.famesmart.privilege.mapper.UserCommAlarmsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiaxu.Li
 * @since 2021-05-27
 */
@Service
public class UserCommAlarmsService extends ServiceImpl<UserCommAlarmsMapper, UserCommAlarms> {

    @Resource
    private UserCommAlarmsMapper userCommAlarmsMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUserCommAlarm(Integer userId, Integer commAlarmId) {
        UserCommAlarms userCommAlarms = new UserCommAlarms();
        userCommAlarms.setSaasUserId(userId);
        userCommAlarms.setCommAlarmId(commAlarmId);
        save(userCommAlarms);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CommAlarmVO> getCommAlarmByUserId(Integer userId) {
        return userCommAlarmsMapper.selectCommAlarmByUserId(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByUserId(Integer userId) {
        QueryWrapper<UserCommAlarms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        userCommAlarmsMapper.delete(queryWrapper);
    }
}
