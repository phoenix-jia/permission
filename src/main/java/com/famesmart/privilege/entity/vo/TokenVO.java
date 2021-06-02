package com.famesmart.privilege.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenVO {

    private String token;

    private String refreshToken;
}
