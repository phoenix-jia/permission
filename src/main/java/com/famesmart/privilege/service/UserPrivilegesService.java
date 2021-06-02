package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.UserPrivileges;
import com.famesmart.privilege.mapper.UserPrivilegesMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserPrivilegesService extends ServiceImpl<UserPrivilegesMapper, UserPrivileges> {

    @Resource
    private UserPrivilegesMapper userPrivilegesMapper;

    public void deleteByPrivilegeId(Integer id) {
        QueryWrapper<UserPrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_privilege_id", id);
        remove(queryWrapper);
    }

    public void insertUserPrivilege(Integer id, Integer privilegeId) {
        UserPrivileges userPrivilege = new UserPrivileges();
        userPrivilege.setSaasUserId(id);
        userPrivilege.setSaasPrivilegeId(privilegeId);
        userPrivilegesMapper.insert(userPrivilege);
    }

    public List<UserPrivileges> selectByUserId(Integer userId) {
        QueryWrapper<UserPrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        return userPrivilegesMapper.selectList(queryWrapper);
    }

    public void deleteByUserId(Integer userId) {
        QueryWrapper<UserPrivileges> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        userPrivilegesMapper.delete(queryWrapper);
    }

    public List<Privileges> getPrivilegeByUserId(Integer userId) {
        return userPrivilegesMapper.selectPrivilegeByUserId(userId);
    }

    public boolean checkPrivilege(Integer userId, String resource, String operation) {
        List<Privileges> privileges = userPrivilegesMapper.selectPrivilegeByUserId(userId);
        return privileges.stream().anyMatch(privilege ->
                privilege.getResource().equals(resource) && privilege.getOperation().equals(operation));
    }
}
