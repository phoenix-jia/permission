package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.UserRoles;
import com.famesmart.privilege.mapper.UserRolesMapper;
import com.famesmart.privilege.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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
public class UserRolesService extends ServiceImpl<UserRolesMapper, UserRoles> {

    static final String userRolePrefix = "userRole:";

    @Resource
    private UserRolesMapper userRolesMapper;

    @Autowired
    private RolesService rolesService;

    @Autowired
    StringRedisTemplate redisTemplate;

    public void insertUserRole(Integer userId, Integer roleId) {
        UserRoles userRole = new UserRoles();
        userRole.setSaasUserId(userId);
        userRole.setSaasRoleId(roleId);
        userRolesMapper.insert(userRole);
        invalidCache(userId);
    }

    public List<UserRoles> selectByUserId(Integer userId) {
        String key = userRolePrefix + userId;
        String userRolesStr = redisTemplate.opsForValue().get(key);
        List<UserRoles> userRoles;
        if (StringUtils.isNotBlank(userRolesStr)) {
            userRoles = JsonUtils.jsonToList(userRolesStr, UserRoles.class);
        } else {
            QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("saas_user_id", userId);
            userRoles = userRolesMapper.selectList(queryWrapper);
            userRolesStr = JsonUtils.objectToJson(userRoles);
            userRolesStr = userRolesStr != null ? userRolesStr : "null";
            redisTemplate.opsForValue().set(key, userRolesStr, 1, TimeUnit.HOURS);
        }
        return userRoles;
    }

    public void deleteByUserId(Integer userId) {
        QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        userRolesMapper.delete(queryWrapper);
        invalidCache(userId);
    }

    public void deleteByRoleId(Integer roleId) {
        QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_role_id", roleId);
        List<UserRoles> userRoles = userRolesMapper.selectList(queryWrapper);
        for (UserRoles userRole : userRoles) {
            invalidCache(userRole.getSaasUserId());
            userRolesMapper.deleteById(userRole.getId());
        }
    }

    public void deleteByUserIdAndRoleId(Integer userId, Integer roleId) {
        QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        queryWrapper.eq("saas_role_id", roleId);
        userRolesMapper.delete(queryWrapper);
        invalidCache(userId);
    }

    public boolean checkPrivilege(Integer userId, String resource, String operation) {
        return selectByUserId(userId).stream()
                .map(UserRoles::getSaasRoleId)
                .map(roleId -> rolesService.getRoleVOById(roleId))
                .flatMap(roleVO -> roleVO.getPrivileges().stream())
                .anyMatch(privilege -> privilege.getResource().equals(resource)
                        && privilege.getOperation().equals(operation));
    }

    private void invalidCache(Integer userId) {
        String key = userRolePrefix + userId;
        redisTemplate.delete(key);
    }
}
