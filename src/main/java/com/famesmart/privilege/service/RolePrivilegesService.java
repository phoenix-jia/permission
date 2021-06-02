package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.RolePrivileges;
import com.famesmart.privilege.entity.UserPrivileges;
import com.famesmart.privilege.mapper.RolePrivilegesMapper;
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
public class RolePrivilegesService extends ServiceImpl<RolePrivilegesMapper, RolePrivileges> {

    @Resource
    private RolePrivilegesMapper rolePrivilegesMapper;

    public void deleteByPrivilegeId(Integer id) {
        QueryWrapper<RolePrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_privilege_id", id);
        remove(queryWrapper);
    }

    public void insertRolePrivilege(Integer id, Integer privilegeId) {
        RolePrivileges rolePrivilege = new RolePrivileges();
        rolePrivilege.setSaasRoleId(id);
        rolePrivilege.setSaasPrivilegeId(privilegeId);
        rolePrivilegesMapper.insert(rolePrivilege);
    }

    public List<RolePrivileges> selectByRoleId(Integer id) {
        return rolePrivilegesMapper.selectByRoleId(id);
    }
}
