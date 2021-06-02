package com.famesmart.privilege.controller;

import com.famesmart.privilege.entity.bo.ToMessageBO;
import com.famesmart.privilege.security.UserDetailsCustom;
import com.famesmart.privilege.service.WsService;
import com.famesmart.privilege.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = "推送")
@RestController
@RequestMapping("/ws-push")
public class WsController {

    @Autowired
    private WsService wsService;

    @PostMapping
    @ApiOperation(value = "ws推送", httpMethod = "POST")
    public Result addUser(@ApiIgnore @AuthenticationPrincipal UserDetailsCustom userDetailsCustom,
                          @RequestBody @Valid ToMessageBO toMessageBO,
                          @ApiIgnore BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error("missing toUser/toComm");
        }

        if (StringUtils.isBlank(toMessageBO.getFromUser())) {
            toMessageBO.setFromUser(userDetailsCustom.getUsername());
        }

        wsService.push(toMessageBO);
        return Result.ok();
    }
}
