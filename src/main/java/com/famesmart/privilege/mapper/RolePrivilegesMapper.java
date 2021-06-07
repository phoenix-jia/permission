package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.RolePrivileges;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
public interface RolePrivilegesMapper extends BaseMapper<RolePrivileges> {

    @Select("select * from v2_saas_role_privileges where saas_role_id = #{roleId} and deleted_at is null")
    List<RolePrivileges> selectByRoleId(Integer roleId);
}
