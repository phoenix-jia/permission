package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.Comms;
import com.famesmart.privilege.entity.UserComms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.famesmart.privilege.entity.Users;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
public interface UserCommsMapper extends BaseMapper<UserComms> {

    List<Comms> selectCommByUserId(Integer userId);

    List<Users> selectUserByCommCode(String commCode);
}
