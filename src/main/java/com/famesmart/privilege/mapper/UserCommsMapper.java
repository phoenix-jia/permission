package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.Comms;
import com.famesmart.privilege.entity.UserComms;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.famesmart.privilege.entity.Users;

import java.util.List;

public interface UserCommsMapper extends BaseMapper<UserComms> {

    List<Comms> selectCommByUserId(Integer userId);

    List<Users> selectUserByCommCode(String commCode);
}
