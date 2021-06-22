package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.mapper.PrivilegesMapper;
import com.famesmart.privilege.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
@Service
public class PrivilegesService extends ServiceImpl<PrivilegesMapper, Privileges> implements ApplicationListener<ContextRefreshedEvent> {

    static final String privilegeKey = "privilege";

    @Resource
    private PrivilegesMapper privilegesMapper;

    @Autowired
    private UserPrivilegesService userPrivilegesService;

    @Autowired
    private RolePrivilegesService rolePrivilegesService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent contextRefreshedEvent) {
        redisTemplate.delete(privilegeKey);
        List<Privileges> privileges = list();
        for (Privileges privilege : privileges) {
            updateCache(privilege);
        }
    }

    public void addPrivilege(Privileges privilege) {
        save(privilege);
        getAndUpdateCache(privilege.getId());
    }

    public List<Privileges> getMulti(List<Integer> ids) {
        List<Object> objects = ids.stream().map(Object::toString).collect(Collectors.toList());
        List<Object> list = redisTemplate.opsForHash().multiGet(privilegeKey, objects);
        List<Privileges> result = new ArrayList<>();
        for (Object value : list) {
            Privileges privilege = JsonUtils.jsonToObject((String) value, Privileges.class);
            result.add(privilege);
        }
        return result;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Privileges getById(Integer id) {
        String privilegeStr = (String) redisTemplate.opsForHash().get(privilegeKey, id.toString());
        if (StringUtils.isNotBlank(privilegeStr)) {
            return JsonUtils.jsonToObject(privilegeStr, Privileges.class);
        } else {
            return getAndUpdateCache(id);
        }
    }

    public void update(Privileges privilege) {
        updateById(privilege);
        getAndUpdateCache(privilege.getId());
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteById(Integer id) {
        privilegesMapper.deleteById(id);
        userPrivilegesService.deleteByPrivilegeId(id);
        rolePrivilegesService.deleteByPrivilegeId(id);
        deleteCache(id);
    }

    public List<String> selectAllResource() {
        return privilegesMapper.selectAllResource();
    }

    public List<Privileges> selectByResource(String resource) {
        return privilegesMapper.selectByResource(resource);
    }

    public Privileges getAndUpdateCache(Integer id) {
        Privileges privilege = privilegesMapper.selectById(id);
        updateCache(privilege);
        return privilege;
    }

    public void updateCache(Privileges privilege) {
        String privilegeStr = JsonUtils.objectToJson(privilege);
        redisTemplate.opsForHash().put(privilegeKey, privilege.getId().toString(), privilegeStr);
    }

    public void deleteCache(Integer id) {
        redisTemplate.opsForHash().delete(privilegeKey, id.toString());
    }
}
