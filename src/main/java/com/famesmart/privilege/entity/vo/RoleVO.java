package com.famesmart.privilege.entity.vo;

import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.Roles;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleVO extends Roles {

    private List<Privileges> privileges;
}
