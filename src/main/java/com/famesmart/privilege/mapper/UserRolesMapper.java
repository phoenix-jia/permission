package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.UserRoles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

public interface UserRolesMapper extends BaseMapper<UserRoles> {

    Privileges selectPrivilege(Integer userId, String resource, String operation);
}
