package com.famesmart.privilege.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
public interface UsersMapper extends BaseMapper<Users> {

    IPage<Users> selectUserList(Page<Users> usersPage, String position, Integer isAdmin, String commCode);

    Users selectCommAdmin(String commCode);
}
