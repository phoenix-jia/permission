package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.famesmart.privilege.entity.Comms;
import com.famesmart.privilege.entity.Users;
import com.famesmart.privilege.mapper.CommsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */
@Service
public class CommsService extends ServiceImpl<CommsMapper, Comms> {

    @Resource
    private CommsMapper commsMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    public Comms selectByCommCode(String commCode) {
        QueryWrapper<Comms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comm_code", commCode);
        return commsMapper.selectOne(queryWrapper);
    }
}
