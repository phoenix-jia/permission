package com.famesmart.privilege.entity.bo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ToMessageBO {

    @NotBlank
    private String toUser;

    @NotBlank
    private String toComm;

    private String type;

    private String data;

    private String fromUser;
}
