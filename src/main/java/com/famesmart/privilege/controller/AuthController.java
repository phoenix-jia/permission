package com.famesmart.privilege.controller;

import com.famesmart.privilege.entity.bo.LoginBO;
import com.famesmart.privilege.security.UserDetailsCustom;
import com.famesmart.privilege.service.AuthService;
import com.famesmart.privilege.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Api(tags = "登录")
@RestController
@RequestMapping
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    @ApiOperation(value = "登录", httpMethod = "POST")
    public Result login(@RequestBody @Valid LoginBO loginBO,
                        @ApiIgnore BindingResult bindingResult,
                        @ApiIgnore HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            return Result.error("invalid username or password");
        }
        return Result.ok(authService.login(loginBO, request));
    }

    @GetMapping("/refresh")
    @ApiOperation(value = "更新token", httpMethod = "GET")
    public Result refresh(@ApiIgnore @AuthenticationPrincipal UserDetailsCustom userDetailsCustom){
        return Result.ok(authService.refresh(userDetailsCustom));
    }
}
