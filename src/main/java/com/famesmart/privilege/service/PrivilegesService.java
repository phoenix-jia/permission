package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.UserPrivileges;
import com.famesmart.privilege.mapper.PrivilegesMapper;
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
public class PrivilegesService extends ServiceImpl<PrivilegesMapper, Privileges> {

    @Resource
    private PrivilegesMapper privilegesMapper;

    @Autowired
    private UserPrivilegesService userPrivilegesService;

    @Autowired
    private RolePrivilegesService rolePrivilegesService;

    public void deleteById(Integer id) {
        removeById(id);
        userPrivilegesService.deleteByPrivilegeId(id);
        rolePrivilegesService.deleteByPrivilegeId(id);
    }

    public List<String> selectAllResource() {
        return privilegesMapper.selectAllResource();
    }

    public List<Privileges> selectByResource(String resource) {
        return privilegesMapper.selectByResource(resource);
    }
}
