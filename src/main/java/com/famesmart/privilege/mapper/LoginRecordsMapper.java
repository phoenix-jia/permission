package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.LoginRecords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.famesmart.privilege.entity.vo.LoginTimesVO;

public interface LoginRecordsMapper extends BaseMapper<LoginRecords> {

    LoginTimesVO selectLoginTimes(String username);
}
