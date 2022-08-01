package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.Comms;
import com.famesmart.privilege.entity.UserComms;
import com.famesmart.privilege.entity.UserRoles;
import com.famesmart.privilege.entity.Users;
import com.famesmart.privilege.mapper.UserCommsMapper;
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
public class UserCommsService extends ServiceImpl<UserCommsMapper, UserComms> {

    @Resource
    private UserCommsMapper userCommsMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Comms> getCommByUserId(Integer userId) {
        return userCommsMapper.selectCommByUserId(userId);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void insertUserComm(Integer userId, Integer commId) {
        UserComms userComms = new UserComms();
        userComms.setSaasUserId(userId);
        userComms.setCommId(commId);
        save(userComms);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Users> getUserByCommCode(String commCode) {
        return userCommsMapper.selectUserByCommCode(commCode);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByUserId(Integer userId) {
        QueryWrapper<UserComms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        userCommsMapper.delete(queryWrapper);
    }
}
