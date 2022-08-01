package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.famesmart.privilege.entity.LoginRecords;
import com.famesmart.privilege.entity.bo.TalkMessageBO;
import com.famesmart.privilege.entity.vo.LoginTimesVO;
import com.famesmart.privilege.mapper.LoginRecordsMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jiaxu.Li
 * @since 2021-05-28
 */
@Service
public class LoginRecordsService extends ServiceImpl<LoginRecordsMapper, LoginRecords> {

    static final String dingTalkUrl = "https://oapi.dingtalk.com/robot/send?access_token=c0a54231eb7a24db78fad8ea4fc0e1c593aab8435c07c2f5812b98393e996f20";

    @Resource
    private LoginRecordsMapper loginRecordsMapper;

   @Autowired
   private RestTemplate restTemplate;

    @Transactional(propagation = Propagation.SUPPORTS)
    public IPage<LoginRecords> queryUserLoginRecord(Integer pageNum, Integer pageSize, String username, String clientIp, String clientAddress, String commCode) {
        Page<LoginRecords> page = new Page<>(pageNum, pageSize);
        QueryWrapper<LoginRecords> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(username), "username", username);
        queryWrapper.eq(StringUtils.isNotBlank(clientIp), "client_ip", clientIp);
        queryWrapper.eq(StringUtils.isNotBlank(clientAddress), "client_address", clientAddress);
        queryWrapper.eq(StringUtils.isNotBlank(commCode), "project_code", commCode);
        queryWrapper.orderByDesc("created_at");
        return loginRecordsMapper.selectPage(page, queryWrapper);
    }

    public void addLoginRecord(String username, String ip) {
        LoginTimesVO loginTimesVO = loginRecordsMapper.selectLoginTimes(username);

        if (loginTimesVO != null && !loginTimesVO.getClientIp().equals(ip)) {
            TalkMessageBO msgBO = new TalkMessageBO("text", new TalkMessageBO.Text(String.format("检测到saas异常ip登录, 用户名: %s, ip: %s, 时间: %s", username, ip, new Date())));
            restTemplate.postForLocation(dingTalkUrl, msgBO);
        }

        LoginRecords loginRecords = LoginRecords.builder().username(username).clientIp(ip).clientAddress("").build();
        loginRecordsMapper.insert(loginRecords);
    }

}
