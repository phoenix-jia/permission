package com.famesmart.privilege.controller;

import com.famesmart.privilege.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;
import com.famesmart.privilege.service.LoginRecordsService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Jianhang.Wu
 * @since 2021-05-28
 */

@Api(tags = "登录记录")
@RestController
@RequestMapping("/loginRecord")
public class LoginRecordsController {

    @Autowired
    private LoginRecordsService loginRecordsService;

    @GetMapping
    @ApiOperation(value = "用户登录记录获取", httpMethod = "GET")
    public Result getUserLoginRecord(@ApiParam(name = "pageNum", value = "第几页")
                                     @RequestParam(defaultValue = "1") Integer pageNum,
                                     @ApiParam(name = "pageSize", value = "每一页显示的条数")
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     @ApiParam(name = "username", value = "用户名", required = false)
                                     @RequestParam(required = false) String username,
                                     @ApiParam(name = "clientIp", value = "用户IP", required = false)
                                     @RequestParam(required = false) String clientIp,
                                     @ApiParam(name = "clientAddress", value = "用户地址", required = false)
                                     @RequestParam(required = false) String clientAddress,
                                     @ApiParam(name = "commCode", value = "社区代码", required = false)
                                     @RequestParam(required = false) String commCode) {

        return Result.ok(loginRecordsService.queryUserLoginRecord(pageNum, pageSize, username, clientIp, clientAddress, commCode));
    }
}



