package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.RolePrivileges;
import com.famesmart.privilege.entity.Roles;
import com.famesmart.privilege.entity.bo.RoleBO;
import com.famesmart.privilege.entity.vo.RoleVO;
import com.famesmart.privilege.mapper.RolesMapper;
import com.famesmart.privilege.util.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
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
 * @author Jiaxu.Li
 * @since 2021-05-27
 */
@Service
public class RolesService extends ServiceImpl<RolesMapper, Roles> implements ApplicationListener<ContextRefreshedEvent> {

    static final String roleKey = "saas_role";

    @Resource
    private RolesMapper rolesMapper;

    @Autowired
    private PrivilegesService privilegesService;

    @Autowired
    private RolePrivilegesService rolePrivilegesService;

    @Autowired
    private UserRolesService userRolesService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent contextRefreshedEvent) {
        redisTemplate.delete(roleKey);
        List<Roles> roles = list();
        for (Roles role : roles) {
            updateCache(role);
        }
    }

    public RoleVO getRoleVOByIdOrName(Integer id, String name) {
        return id != null ? getRoleVOById(id) : getRoleVOByName(name);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public RoleVO getRoleVOByName(String name) {
        QueryWrapper<Roles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        Roles role = rolesMapper.selectOne(queryWrapper);
        return getRoleVOById(role.getId());
    }

    public RoleVO getRoleVOById(Integer id) {
        Roles role = getRoleById(id);
        return getRoleVO(role);
    }

    public RoleVO getRoleVO(Roles role) {
        RoleVO roleVO = modelMapper.map(role, RoleVO.class);
        roleVO.setPrivileges(getPrivilegesById(role.getId()));
        return roleVO;
    }

    public List<Privileges> getPrivilegesById(Integer id) {
        List<Integer> privileges = rolePrivilegesService
                .selectByRoleId(id)
                .stream()
                .map(RolePrivileges::getSaasPrivilegeId)
                .collect(Collectors.toList());
        return privilegesService.getMulti(privileges);
    }

    public Roles getRoleById(Integer id) {
        String roleStr = (String) redisTemplate.opsForHash().get(roleKey, id.toString());
        if (StringUtils.isNotBlank(roleStr)) {
            return JsonUtils.jsonToObject(roleStr, Roles.class);
        } else {
            return getAndUpdateCache(id);
        }
    }

    public List<Roles> getMulti(List<Integer> ids) {
        List<Object> objects = ids.stream().map(Object::toString).collect(Collectors.toList());
        List<Object> list = redisTemplate.opsForHash().multiGet(roleKey, objects);
        List<Roles> result = new ArrayList<>();
        for (Object value : list) {
            Roles role = JsonUtils.jsonToObject((String) value, Roles.class);
            result.add(role);
        }
        return result;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Roles addRole(RoleBO roleBO) {
        List<Integer> privilegeIdList = roleBO.getPrivilegeIds();
        if (privilegeIdList != null && checkPrivilegeIdList(privilegeIdList)) {
            throw new RuntimeException("invalid privilege id found");
        }

        Roles role = modelMapper.map(roleBO, Roles.class);
        save(role);

        if (privilegeIdList != null) {
            for (Integer privilegeId : privilegeIdList) {
                rolePrivilegesService.insertRolePrivilege(role.getId(), privilegeId);
            }
        }
        getAndUpdateCache(role.getId());
        return role;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Integer id, RoleBO roleBO) {
        List<Integer> privilegeIdList = roleBO.getPrivilegeIds();
        if (privilegeIdList != null && checkPrivilegeIdList(privilegeIdList)) {
            throw new RuntimeException("invalid privilege id found");
        }

        if (StringUtils.isNotBlank(roleBO.getName())) {
            Roles role = modelMapper.map(roleBO, Roles.class);
            role.setId(id);
            updateById(role);
        }

        if (privilegeIdList != null) {
            Set<Integer> privilegeIdSet = rolePrivilegesService.selectByRoleId(id)
                    .stream().map(RolePrivileges::getSaasPrivilegeId).collect(Collectors.toSet());
            privilegeIdList.stream().distinct().forEach(privilegeId -> {
                if (privilegeIdSet.contains(privilegeId)) {
                    privilegeIdSet.remove(privilegeId);
                } else {
                    rolePrivilegesService.insertRolePrivilege(id, privilegeId);
                }
            });
            privilegeIdSet.forEach(privilegeId -> {
                rolePrivilegesService.deleteByRoleIdAndPrivilegeId(id, privilegeId);
            });
        }

        getAndUpdateCache(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer id) {
        removeById(id);
        userRolesService.deleteByRoleId(id);
        rolePrivilegesService.deleteByRoleId(id);
        deleteCache(id);
    }

    public boolean checkPrivilegeIdList(List<Integer> privilegeIdList) {
        return privilegesService.getMulti(privilegeIdList).stream().anyMatch(Objects::isNull);
    }

    public Roles getAndUpdateCache(Integer id) {
        Roles role = rolesMapper.selectById(id);
        updateCache(role);
        return role;
    }

    public void updateCache(Roles role) {
        String roleStr = JsonUtils.objectToJson(role);
        redisTemplate.opsForHash().put(roleKey, role.getId().toString(), roleStr);
    }

    public void deleteCache(Integer id) {
        redisTemplate.opsForHash().delete(roleKey, id.toString());
    }
}
