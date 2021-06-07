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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
public class RolesService extends ServiceImpl<RolesMapper, Roles> {

    static final String rolePrefix = "role:";

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

    public RoleVO getRoleVOByIdOrName(Integer id, String name) {
        return id != null ? getRoleVOById(id) : getRoleVOByName(name);
    }

    private RoleVO getRoleVOByName(String name) {
        QueryWrapper<Roles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", name);
        Roles role = rolesMapper.selectOne(queryWrapper);
        return getRoleVOById(role.getId());
    }

    public RoleVO getRoleVOById(Integer id) {
        Roles role = getRoleById(id);
        RoleVO roleVO = modelMapper.map(role, RoleVO.class);
        List<Privileges> privileges =  rolePrivilegesService.selectByRoleId(id).parallelStream()
                .map(RolePrivileges::getSaasPrivilegeId)
                .map(privilegesService::getById)
                .collect(Collectors.toList());
        roleVO.setPrivileges(privileges);
        return roleVO;
    }

    public Roles getRoleById(Integer id) {
        String key = rolePrefix + id;
        String roleStr = redisTemplate.opsForValue().get(key);
        Roles role;
        if (StringUtils.isNotBlank(roleStr)) {
            role = JsonUtils.jsonToObject(roleStr, Roles.class);
        } else {
            role = rolesMapper.selectById(id);
            roleStr = JsonUtils.objectToJson(role);
            roleStr = roleStr != null ? roleStr : "null";
            redisTemplate.opsForValue().set(key, roleStr, 1, TimeUnit.HOURS);
        }
        return role;
    }

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
        return role;
    }

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

        invalidCache(id);
    }

    public void deleteRole(Integer id) {
        removeById(id);
        userRolesService.deleteByRoleId(id);
        rolePrivilegesService.deleteByRoleId(id);
        invalidCache(id);
    }

    public boolean checkPrivilegeIdList(List<Integer> privilegeIdList) {
        return !privilegeIdList.parallelStream().allMatch(privilegeId ->
                privilegesService.getById(privilegeId) != null);
    }

    public void invalidCache(Integer id) {
        redisTemplate.delete(rolePrefix + id);
    }
}
