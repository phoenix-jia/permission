package com.famesmart.privilege.entity.vo;

import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.Roles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleVO extends Roles {

    private List<Privileges> privileges;
}
