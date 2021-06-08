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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
public class PrivilegesService extends ServiceImpl<PrivilegesMapper, Privileges> {

    static final String privilegePrefix = "privilege:";

    @Resource
    private PrivilegesMapper privilegesMapper;

    @Autowired
    private UserPrivilegesService userPrivilegesService;

    @Autowired
    private RolePrivilegesService rolePrivilegesService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Privileges getById(Integer id) {
        String key = privilegePrefix + id;
        String privilegeStr = redisTemplate.opsForValue().get(key);
        Privileges privilege;
        if (StringUtils.isNotBlank(privilegeStr)) {
            privilege = JsonUtils.jsonToObject(privilegeStr, Privileges.class);
        } else {
            privilege = privilegesMapper.selectById(id);
            privilegeStr = JsonUtils.objectToJson(privilege);
            privilegeStr = privilegeStr != null ? privilegeStr : "null";
            redisTemplate.opsForValue().set(key, privilegeStr, 12, TimeUnit.HOURS);
        }
        return privilege;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(Integer id) {
        privilegesMapper.deleteById(id);
        userPrivilegesService.deleteByPrivilegeId(id);
        rolePrivilegesService.deleteByPrivilegeId(id);
        invalidCache(id);
    }

    public List<String> selectAllResource() {
        return privilegesMapper.selectAllResource();
    }

    public List<Privileges> selectByResource(String resource) {
        return privilegesMapper.selectByResource(resource);
    }

    public void invalidCache(Integer id) {
        redisTemplate.delete(privilegePrefix + id);
    }
}
