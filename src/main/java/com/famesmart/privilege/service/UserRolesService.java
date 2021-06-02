package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.UserRoles;
import com.famesmart.privilege.mapper.UserRolesMapper;
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
public class UserRolesService extends ServiceImpl<UserRolesMapper, UserRoles> {

    @Resource
    private UserRolesMapper userRolesMapper;

    public void deleteByRoleId(Integer roleId) {
        QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_role_id", roleId);
        userRolesMapper.delete(queryWrapper);
    }

    public void insertUserRole(Integer id, Integer roleId) {
        UserRoles userRole = new UserRoles();
        userRole.setSaasUserId(id);
        userRole.setSaasRoleId(roleId);
        userRolesMapper.insert(userRole);
    }

    public List<UserRoles> selectByUserId(Integer userId) {
        QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        return userRolesMapper.selectList(queryWrapper);
    }

    public void deleteByUserId(Integer userId) {
        QueryWrapper<UserRoles> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("saas_user_id", userId);
        userRolesMapper.delete(queryWrapper);
    }

    public boolean checkPrivilege(Integer userId, String resource, String operation) {
        return userRolesMapper.selectPrivilege(userId, resource, operation) != null;
    }
}
