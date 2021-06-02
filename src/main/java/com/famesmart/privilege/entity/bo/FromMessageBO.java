package com.famesmart.privilege.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FromMessageBO {

    private String fromUser;

    private String type;

    private String data;
}
