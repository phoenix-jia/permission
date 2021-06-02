package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.UserRoles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
public interface UserRolesMapper extends BaseMapper<UserRoles> {

    Privileges selectPrivilege(Integer userId, String resource, String operation);
}
