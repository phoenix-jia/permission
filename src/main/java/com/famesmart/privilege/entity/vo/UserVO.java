package com.famesmart.privilege.entity.vo;

import com.famesmart.privilege.entity.Comms;
import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.Users;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class UserVO extends Users {

    private List<RoleVO> roles;

    private List<Privileges> privileges;

    private List<Comms> comms;
}
