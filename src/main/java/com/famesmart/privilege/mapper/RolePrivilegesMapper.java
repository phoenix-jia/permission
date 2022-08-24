package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.RolePrivileges;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RolePrivilegesMapper extends BaseMapper<RolePrivileges> {

    @Select("select * from v2_saas_role_privileges where saas_role_id = #{roleId} and deleted_at is null")
    List<RolePrivileges> selectByRoleId(Integer roleId);
}
