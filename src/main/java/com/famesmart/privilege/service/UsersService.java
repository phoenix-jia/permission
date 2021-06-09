package com.famesmart.privilege.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.famesmart.privilege.entity.*;
import com.famesmart.privilege.entity.bo.*;
import com.famesmart.privilege.entity.vo.CommAlarmVO;
import com.famesmart.privilege.entity.vo.RoleVO;
import com.famesmart.privilege.entity.vo.UserVO;
import com.famesmart.privilege.mapper.UsersMapper;
import com.famesmart.privilege.security.UserDetailsCustom;
import com.famesmart.privilege.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-27
 */

@Slf4j
@Service
public class UsersService extends BaseService {

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private UserDetailsCustomService userDetailsCustomService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private PrivilegesService privilegesService;

    @Autowired
    private CommsService commsService;

    @Autowired
    private UserRolesService userRolesService;

    @Autowired
    private UserPrivilegesService userPrivilegesService;

    @Autowired
    private CommAlarmsService commAlarmsService;

    @Autowired
    private UserCommAlarmsService userCommAlarmsService;

    @Autowired
    private UserCommsService userCommsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    public static String basicInfoUrl = "http://api.saas.famesmart.com/basic-infos/smsSend";

    @Transactional(propagation = Propagation.SUPPORTS)
    public IPage<Users> queryUserList(Integer pageNum, Integer pageSize, String position, Integer isAdmin, String commCode) {
        Page<Users> usersPage = new Page<>(pageNum, pageSize);
        return usersMapper.selectUserList(usersPage, position, isAdmin, commCode);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public UserVO getByIdOrUsername(Integer id, String username) {

        UserDetails userDetails = id != null
                ? userDetailsCustomService.loadUserById(id.toString())
                : userDetailsCustomService.loadUserByUsername(username);

        if (userDetails == null) {
            return null;
        }

        UserDetailsCustom user = (UserDetailsCustom) userDetails;
        UserVO userVO = modelMapper.map(user, UserVO.class);
        userVO.setRoles(getRoles(user));
        userVO.setPrivileges(getPrivileges(user));
        userVO.setComms(userCommsService.getCommByUserId(user.getId()));

        return userVO;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users getByPhone(String phone) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        List<Users> users = usersMapper.selectList(queryWrapper);
        return users == null || users.isEmpty() ? null : users.get(0);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users getByUsername(String username) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return usersMapper.selectOne(queryWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Users addUser(UserBO userBO) {
        if (getByUsername(userBO.getUsername()) != null) {
            throw new RuntimeException("user already existed");
        }

        checkUserList(userBO);
        List<Integer> roleIdList = userBO.getRoleIds();
        List<Integer> privilegeIdList = userBO.getPrivilegeIds();

        String commCode = userBO.getCommCode();
        Comms comm = getComm(commCode);
        if (commCode != null && comm == null) {
            throw new RuntimeException("invalid community code");
        }

        Users user = modelMapper.map(userBO, Users.class);
        usersMapper.insert(user);

        if (roleIdList != null) {
            for (Integer roleId : roleIdList) {
                userRolesService.insertUserRole(user.getId(), roleId);
            }
        }

        if (privilegeIdList != null) {
            for (Integer privilegeId : privilegeIdList) {
                userPrivilegesService.insertUserPrivilege(user.getId(), privilegeId);
            }
        }

        if (commCode != null) {
            userCommsService.insertUserComm(user.getId(), comm.getId());
        }

        return user;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(Integer id, UserBO userBO) {
        checkUserList(userBO);
        List<Integer> roleIdList = userBO.getRoleIds();
        List<Integer> privilegeIdList = userBO.getPrivilegeIds();

        Users user = modelMapper.map(userBO, Users.class);
        user.setId(id);
        usersMapper.updateById(user);

        if (roleIdList != null) {
            Set<Integer> userRoleIdSet = userRolesService.selectByUserId(id)
                    .stream().map(UserRoles::getSaasRoleId).collect(Collectors.toSet());
            roleIdList.stream().distinct().forEach(roleId -> {
                if (userRoleIdSet.contains(roleId)) {
                    userRoleIdSet.remove(roleId);
                } else {
                    userRolesService.insertUserRole(id, roleId);
                }
            });
            userRoleIdSet.forEach(roleId -> {
                userRolesService.deleteByUserIdAndRoleId(id, roleId);
            });
        }

        if (privilegeIdList != null) {
            Set<Integer> userPrivilegeIdSet = userPrivilegesService.selectByUserId(id)
                    .stream().map(UserPrivileges::getSaasPrivilegeId).collect(Collectors.toSet());
            privilegeIdList.stream().distinct().forEach(userPrivilegeId -> {
                if (userPrivilegeIdSet.contains(userPrivilegeId)) {
                    userPrivilegeIdSet.remove(userPrivilegeId);
                } else {
                    userPrivilegesService.insertUserPrivilege(id, userPrivilegeId);
                }
            });
            userPrivilegeIdSet.forEach(privilegeId -> {
                userPrivilegesService.deleteByUserIdAndPrivilegeId(id, privilegeId);
            });
        }

        invalidCache(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUser(Integer id) {
        usersMapper.deleteById(id);
        userRolesService.deleteByUserId(id);
        userPrivilegesService.deleteByUserId(id);
        invalidCache(id);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addCommAdmin(UserBO userBO) {
        userBO.setRoleIds(Collections.singletonList(1));
        addUser(userBO);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public Users getCommAdmin(String commCode) {
        return usersMapper.selectCommAdmin(commCode);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addUserCommAlarm(UserCommAlarmBO userCommAlarmBO) {
        List<Integer> commAlarmIdList = userCommAlarmBO.getCommAlarmIds();
        if (commAlarmIdList != null && checkCommAlarmIdList(commAlarmIdList)) {
            throw new RuntimeException("invalid community alarm id found");
        }

        if (commAlarmIdList != null) {
            for (Integer commAlarmId : commAlarmIdList) {
                userCommAlarmsService.insertUserCommAlarm(userCommAlarmBO.getUserId(), commAlarmId);
            }
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<CommAlarmVO> getUserCommAlarm(UserDetailsCustom userDetailsCustom, String username) {
        Users user = null;
        if (StringUtils.isNotBlank(username) && (user = getByUsername(username)) == null) {
            throw new RuntimeException("invalid username");
        }

        Integer userId = user != null ? user.getId() : userDetailsCustom.getId();
        return userCommAlarmsService.getCommAlarmByUserId(userId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Comms> getUserComm(UserDetailsCustom userDetailsCustom, Integer userId, String username) {
        Integer userIdUsed = null;
        if (userId != null) {
            userIdUsed = userId;
        } else if (StringUtils.isNotBlank(username)) {
            Users user = getByUsername(username);
            if (user == null) {
                throw new RuntimeException("invalid username");
            } else {
                userIdUsed = user.getId();
            }
        } else {
            userIdUsed = userDetailsCustom.getId();
        }
        return userCommsService.getCommByUserId(userIdUsed);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addUserComm(UserCommBO userCommBO) {
        List<String> commCodeList = userCommBO.getCommCodes();
        if (commCodeList != null) {
            getCommList(commCodeList).stream().map(Comms::getId).forEach(commId ->
                    userCommsService.insertUserComm(userCommBO.getUserId(), commId)
            );
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void getSensitiveAccess(Integer userId, String phone, String code) {

        if (StringUtils.isBlank(phone)) {
            throw new RuntimeException("此用户手机号未找到,请先完善手机号信息");
        }

        String key = "sms:validation:" + phone;
        String codeSaved = redisTemplate.opsForValue().get(key);

        if (StringUtils.isNotBlank(code)) {
            if (codeSaved == null) {
                throw new RuntimeException("验证码已过期,请重新获取");
            } else if (!codeSaved.equals(code)) {
                throw new RuntimeException("验证失败");
            }
            return;
        }

        if (!checkUserPrivilege(userId, "sensitive_info", "review")) {
            throw new RuntimeException("此用户没有敏感信息权限");
        }

        Integer rand = (new Random()).nextInt(900000) + 100000;
        SmsResultBO result = sendSms(phone, rand);

        if (result.getResult() != 0) {
            throw new RuntimeException("短信服务错误");
        }

        redisTemplate.opsForValue().set(key, rand.toString(), 5, TimeUnit.MINUTES);
    }

    private List<RoleVO> getRoles(UserDetailsCustom user) {
        List<Integer> roleIds = userRolesService.selectByUserId(user.getId()).stream()
                .map(UserRoles::getSaasRoleId).collect(Collectors.toList());

        return rolesService.getMulti(roleIds).parallelStream().map(rolesService::getRoleVO)
                .collect(Collectors.toList());
    }

    private List<Privileges> getPrivileges(UserDetailsCustom user) {
        List<Integer> privilegeIds = userPrivilegesService.selectByUserId(user.getId()).stream()
                .map(UserPrivileges::getSaasPrivilegeId).collect(Collectors.toList());
        return privilegesService.getMulti(privilegeIds);
    }


    public void checkUserList(UserBO userBO) {
        if (userBO.getRoleIds() != null && checkRoleIdList(userBO.getRoleIds())) {
            throw new RuntimeException("invalid role id found");
        }
        if (userBO.getPrivilegeIds() != null && rolesService.checkPrivilegeIdList(userBO.getPrivilegeIds())) {
            throw new RuntimeException("invalid privilege id found");
        }
    }

    public boolean checkRoleIdList(List<Integer> roleIdList) {
        return !roleIdList.parallelStream().allMatch(roleId ->
                rolesService.getById(roleId) != null);
    }

    public Comms getComm(String commCode) {
        return commsService.selectByCommCode(commCode);
    }

    public boolean checkCommAlarmIdList(List<Integer> commAlarmIdList) {
        return !commAlarmIdList.parallelStream().allMatch(commAlarmId ->
                commAlarmsService.getById(commAlarmId) != null);
    }

    public List<Comms> getCommList(List<String> commCodeList) {
        return commCodeList.parallelStream().map(commCode -> {
            Comms comm = commsService.selectByCommCode(commCode);
            if (comm == null) {
                throw new RuntimeException(String.format("invalid community code found: %s", commCode));
            }
            return comm;
        }).collect(Collectors.toList());
    }

    private SmsResultBO sendSms(String phone, Integer rand) {
        SmsMessageBO smsMessageBO = new SmsMessageBO(Collections.singletonList(phone), 55821, Arrays.asList(rand, 5));
        log.info(JsonUtils.objectToJson(smsMessageBO));
        return restTemplate.postForObject(basicInfoUrl, smsMessageBO, SmsResultBO.class);
    }

    public boolean checkUserPrivilege(Integer userId, String resource, String operation) {
        return  userPrivilegesService.checkPrivilege(userId, resource, operation) ||
                userRolesService.checkPrivilege(userId, resource, operation);
    }

    public void invalidCache(Integer id) {
        String key = userPrefix + id;
        redisTemplate.delete(key);
    }
}
