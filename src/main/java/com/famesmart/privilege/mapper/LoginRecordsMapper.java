package com.famesmart.privilege.mapper;

import com.famesmart.privilege.entity.LoginRecords;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.famesmart.privilege.entity.vo.LoginTimesVO;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-28
 */
public interface LoginRecordsMapper extends BaseMapper<LoginRecords> {

    LoginTimesVO selectLoginTimes(String username);
}
