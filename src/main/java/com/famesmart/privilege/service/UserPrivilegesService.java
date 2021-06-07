package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.UserComms;
import com.famesmart.privilege.entity.UserPrivileges;
import com.famesmart.privilege.mapper.PrivilegesMapper;
import com.famesmart.privilege.mapper.UserCommsMapper;
import com.famesmart.privilege.mapper.UserPrivilegesMapper;
import com.famesmart.privilege.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
@Service
public class UserPrivilegesService extends ServiceImpl<UserPrivilegesMapper, UserPrivileges> {

    static final String userPrivilegePrefix = "userPrivilege:";

    @Resource
    private UserPrivilegesMapper userPrivilegesMapper;

    @Resource
    private PrivilegesService privilegesService;

    @Autowired
    StringRedisTemplate redisTemplate;

    public void insertUserPrivilege(Integer userId, Integer privilegeId) {
        UserPrivileges userPrivilege = new UserPrivileges();
        userPrivilege.setSaasUserId(userId);
        userPrivilege.setSaasPrivilegeId(privilegeId);
        userPrivilegesMapper.insert(userPrivilege);
        invalidCache(userId);
    }

    public List<UserPrivileges> selectByUserId(Integer userId) {
        String key = userPrivilegePrefix + userId;
        String userPrivilegeStr = redisTemplate.opsForValue().get(key);
        List<UserPrivileges> userPrivileges = null;
        if (StringUtils.isNotBlank(userPrivilegeStr)) {
            userPrivileges = JsonUtils.jsonToList(userPrivilegeStr, UserPrivileges.class);
        } else {
            QueryWrapper<UserPrivileges> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("saas_user_id", userId);
            userPrivileges = userPrivilegesMapper.selectList(queryWrapper);
            userPrivilegeStr = JsonUtils.objectToJson(userPrivileges);
            userPrivilegeStr = userPrivilegeStr != null ? userPrivilegeStr : "null";
            redisTemplate.opsForValue().set(key, userPrivilegeStr, 1, TimeUnit.HOURS);
        }
        return userPrivileges;
    }

    public void deleteByUserId(Integer userId) {
        QueryWrapper<UserPrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        userPrivilegesMapper.delete(queryWrapper);
        invalidCache(userId);
    }

    public void deleteByPrivilegeId(Integer id) {
        QueryWrapper<UserPrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_privilege_id", id);
        List<UserPrivileges> userPrivileges = userPrivilegesMapper.selectList(queryWrapper);
        for (UserPrivileges userPrivilege : userPrivileges) {
            invalidCache(userPrivilege.getSaasUserId());
            userPrivilegesMapper.deleteById(userPrivilege.getId());
        }
    }

    public void deleteByUserIdAndPrivilegeId(Integer userId, Integer privilegeId) {
        QueryWrapper<UserPrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        queryWrapper.eq("saas_privilege_id", privilegeId);
        userPrivilegesMapper.delete(queryWrapper);
        invalidCache(userId);
    }

    public boolean checkPrivilege(Integer userId, String resource, String operation) {
        return selectByUserId(userId).parallelStream()
                .map(UserPrivileges::getSaasPrivilegeId)
                .map(privilegesService::getById)
                .anyMatch(privilege -> privilege.getResource().equals(resource)
                        && privilege.getOperation().equals(operation));
    }

    private void invalidCache(Integer userId) {
        redisTemplate.delete(userPrivilegePrefix + userId);
    }
}
