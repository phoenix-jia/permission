package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.famesmart.privilege.entity.Users;
import com.famesmart.privilege.mapper.UsersMapper;
import com.famesmart.privilege.security.UserDetailsCustom;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class UserDetailsCustomService extends BaseService implements UserDetailsService {

  @Resource
  private UsersMapper usersMapper;

  @Autowired
  private StringRedisTemplate redisTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Transactional
  public UserDetails loadUserById(String id) throws UsernameNotFoundException {
    String key = userPrefix + id;
    String userStr = redisTemplate.opsForValue().get(key);
    Users user = null;
    try {
      if (StringUtils.isNotBlank(userStr)) {
        user = objectMapper.readValue(userStr, Users.class);
      } else {
        user = usersMapper.selectById(id);
        userStr = objectMapper.writeValueAsString(user);
        redisTemplate.opsForValue().set(key, userStr, 30, TimeUnit.MINUTES);
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    if (user == null) {
      throw new UsernameNotFoundException("User not found with id : " + id);
    }
    return UserDetailsCustom.create(user);
  }

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("username", username);
    Users user = usersMapper.selectOne(queryWrapper);
    if (user == null) {
      throw new UsernameNotFoundException("User not found with username : " + username);
    }
    return UserDetailsCustom.create(user);
  }
}
