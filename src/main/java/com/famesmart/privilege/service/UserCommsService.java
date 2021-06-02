package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.Comms;
import com.famesmart.privilege.entity.UserComms;
import com.famesmart.privilege.entity.UserRoles;
import com.famesmart.privilege.entity.Users;
import com.famesmart.privilege.mapper.UserCommsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
@Service
public class UserCommsService extends ServiceImpl<UserCommsMapper, UserComms> {

    @Resource
    private UserCommsMapper userCommsMapper;

    public List<Comms> getCommByUserId(Integer userId) {
        return userCommsMapper.selectCommByUserId(userId);
    }

    public void insertUserComm(Integer userId, Integer commId) {
        UserComms userComms = new UserComms();
        userComms.setSaasUserId(userId);
        userComms.setCommId(commId);
        save(userComms);
    }

    public List<Users> getUserByCommCode(String commCode) {
        return userCommsMapper.selectUserByCommCode(commCode);
    }

    public void deleteByUserId(Integer userId) {
        QueryWrapper<UserComms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        userCommsMapper.delete(queryWrapper);
    }
}
