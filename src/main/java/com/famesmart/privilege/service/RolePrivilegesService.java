package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.RolePrivileges;
import com.famesmart.privilege.mapper.RolePrivilegesMapper;
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
public class RolePrivilegesService extends ServiceImpl<RolePrivilegesMapper, RolePrivileges> {

    static final String rolePrivilegesPrefix = "rolePrivileges:";

    @Autowired
    StringRedisTemplate redisTemplate;

    @Resource
    private RolePrivilegesMapper rolePrivilegesMapper;

    public void insertRolePrivilege(Integer roleId, Integer privilegeId) {
        RolePrivileges rolePrivilege = new RolePrivileges();
        rolePrivilege.setSaasRoleId(roleId);
        rolePrivilege.setSaasPrivilegeId(privilegeId);
        rolePrivilegesMapper.insert(rolePrivilege);
        invalidCache(roleId);
    }

    public List<RolePrivileges> selectByRoleId(Integer roleId) {
        String key = rolePrivilegesPrefix + roleId;
        String rolePrivilegesStr = redisTemplate.opsForValue().get(key);
        List<RolePrivileges> rolePrivileges;
        if (StringUtils.isNotBlank(rolePrivilegesStr)) {
            rolePrivileges = JsonUtils.jsonToList(rolePrivilegesStr, RolePrivileges.class);
        } else {
            rolePrivileges = rolePrivilegesMapper.selectByRoleId(roleId);
            rolePrivilegesStr = JsonUtils.objectToJson(rolePrivileges);
            rolePrivilegesStr = rolePrivilegesStr != null ? rolePrivilegesStr : "null";
            redisTemplate.opsForValue().set(key, rolePrivilegesStr, 1, TimeUnit.HOURS);
        }
        return rolePrivileges;
    }

    public void deleteByPrivilegeId(Integer privilegeId) {
        QueryWrapper<RolePrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_privilege_id", privilegeId);
        List<RolePrivileges> rolePrivileges = rolePrivilegesMapper.selectList(queryWrapper);
        for (RolePrivileges rolePrivilege : rolePrivileges) {
            rolePrivilegesMapper.deleteById(rolePrivilege.getId());
            invalidCache(rolePrivilege.getSaasRoleId());
        }
    }

    public void deleteByRoleIdAndPrivilegeId(Integer roleId, Integer privilegeId) {
        QueryWrapper<RolePrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_role_id", roleId);
        queryWrapper.eq("saas_privilege_id", privilegeId);
        rolePrivilegesMapper.delete(queryWrapper);
        invalidCache(roleId);
    }

    public void invalidCache(Integer id) {
        redisTemplate.delete(rolePrivilegesPrefix + id);
    }

    public void deleteByRoleId(Integer roleId) {
        QueryWrapper<RolePrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_role_id", roleId);
        rolePrivilegesMapper.delete(queryWrapper);
        invalidCache(roleId);
    }
}
