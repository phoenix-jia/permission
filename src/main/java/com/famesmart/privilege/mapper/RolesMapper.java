package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.Roles;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.famesmart.privilege.entity.vo.RoleVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
public interface RolesMapper extends BaseMapper<Roles> {

    RoleVO selectByIdOrName(Integer id, String name);
}
