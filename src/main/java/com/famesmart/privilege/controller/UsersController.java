package com.famesmart.privilege.controller;

import com.famesmart.privilege.entity.bo.UserBO;
import com.famesmart.privilege.entity.bo.UserCommAlarmBO;
import com.famesmart.privilege.entity.bo.UserCommBO;
import com.famesmart.privilege.security.UserDetailsCustom;
import com.famesmart.privilege.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.famesmart.privilege.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = "用户")
@RestController
@RequestMapping("/users")
public class UsersController {


    @Autowired
    private UsersService usersService;

    @GetMapping("/me")
    @ApiOperation(value = "获取当前用户", httpMethod = "GET")
    public Result me(@ApiIgnore @AuthenticationPrincipal UserDetailsCustom userDetailsCustom){
        return Result.ok(usersService.getByIdOrUsername(userDetailsCustom.getId(), null));
    }

    @GetMapping
    @ApiOperation(value = "用户获取", httpMethod = "GET")
    public Result getUser(@ApiParam(name = "id", value = "用户id", required = false)
                          @RequestParam(required = false) Integer id,
                          @ApiParam(name = "username", value = "用户名", required = false)
                          @RequestParam(required = false) String username) {
        if (id == null && username == null) {
            return Result.error("missing id/username");
        }
        return Result.ok(usersService.getByIdOrUsername(id, username));
    }

    @GetMapping("/getInfoByPhone")
    @ApiOperation(value = "根据手机号获取用户", httpMethod = "GET")
    public Result getUserByPhone(@ApiParam(name = "phone", value = "手机号", required = true)
                                 @RequestParam String phone) {
        return Result.ok(usersService.getByPhone(phone));
    }

    @PostMapping
    @ApiOperation(value = "用户添加", httpMethod = "POST")
    public Result addUser(@ApiIgnore @AuthenticationPrincipal UserDetailsCustom userDetailsCustom,
                          @RequestBody @Valid UserBO userBO,
                          @ApiIgnore BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error("missing username/password/platform");
        }

        if (!userDetailsCustom.getUsername().equals("admin") && userBO.getUsername().startsWith("admin")) {
            return Result.error("invalid username");
        }

        return Result.ok(usersService.addUser(userBO));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "用户更新", httpMethod = "PUT")
    public Result updateUser(@ApiParam(name = "id", value = "用户id", required = true)
                             @PathVariable Integer id, @RequestBody UserBO userBO) {
        usersService.updateUser(id, userBO);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "用户删除", httpMethod = "DELETE")
    public Result deleteUser(@ApiParam(name = "id", value = "用户id", required = true)
                             @PathVariable Integer id) {
        usersService.deleteUser(id);
        return Result.ok();
    }

    @GetMapping(value = "/list")
    @ApiOperation(value = "用户列表获取", httpMethod = "GET")
    public Result list(@ApiParam(name = "pageNum", value = "第几页")
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @ApiParam(name = "pageSize", value = "每一页显示的条数")
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       @ApiParam(name = "position", value = "职位")
                       @RequestParam(required = false) String position,
                       @ApiParam(name = "isAdmin", value = "是否管理员")
                       @RequestParam(required = false) Integer isAdmin,
                       @ApiParam(name = "commCode", value = "社区代码")
                       @RequestParam(required = false) String commCode) {

        return Result.ok(usersService.queryUserList(pageNum, pageSize, position, isAdmin, commCode));
    }

    @PostMapping("/commAdmin")
    @ApiOperation(value = "用户社区管理员添加", httpMethod = "POST")
    public Result addCommAdmin(@ApiIgnore @AuthenticationPrincipal UserDetailsCustom userDetailsCustom,
                               @RequestBody @Valid UserBO userBO,
                               @ApiIgnore BindingResult bindingResult) {
        if (bindingResult.hasErrors() || StringUtils.isBlank(userBO.getCommCode())) {
            return Result.error("missing username/password/platform/commCode");
        }

        usersService.addCommAdmin(userBO);
        return Result.ok();
    }

    @GetMapping("/commAdmin")
    @ApiOperation(value = "用户社区管理员获取", httpMethod = "GET")
    public Result getUserCommAdmin(@ApiParam(name = "commCode", value = "社区代码")
                                   @RequestParam String commCode) {
        return Result.ok(usersService.getCommAdmin(commCode));
    }

    @PostMapping("/commAlarm")
    @ApiOperation(value = "用户社区预警添加", httpMethod = "POST")
    public Result addUserCommAlarm(@RequestBody @Valid UserCommAlarmBO userCommAlarmBO,
                                   @ApiIgnore BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error("missing userId/commAlarmIdList");
        }
        usersService.addUserCommAlarm(userCommAlarmBO);
        return Result.ok();
    }

    @GetMapping("/commAlarm")
    @ApiOperation(value = "用户社区预警获取", httpMethod = "GET")
    public Result getUserCommAlarm(@ApiIgnore @AuthenticationPrincipal UserDetailsCustom userDetailsCustom,
                                   @ApiParam(name = "username", value = "用户名", required = false)
                                   @RequestParam(required = false) String username) {
        return Result.ok(usersService.getUserCommAlarm(userDetailsCustom, username));
    }

    @GetMapping("/comm")
    @ApiOperation(value = "用户社区获取", httpMethod = "GET")
    public Result getUserComm(@ApiIgnore @AuthenticationPrincipal UserDetailsCustom userDetailsCustom,
                                @ApiParam(name = "userId", value = "用户ID", required = false)
                              @RequestParam(required = false) Integer userId,
                              @ApiParam(name = "username", value = "用户名", required = false)
                              @RequestParam(required = false) String username) {
        return Result.ok(usersService.getUserComm(userDetailsCustom, userId, username));
    }

    @PostMapping("/comm")
    @ApiOperation(value = "用户社区添加", httpMethod = "POST")
    public Result addUserComm(@RequestBody UserCommBO userCommBO,
                              @ApiIgnore BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error("missing userId/commCodeList");
        }
        usersService.addUserComm(userCommBO);
        return Result.ok();
    }

    @RequestMapping("/sensitiveInfo")
    @ApiOperation(value = "敏感信息获取", httpMethod = "POST")
    public Result getSensitiveInfo(@ApiIgnore @AuthenticationPrincipal UserDetailsCustom userDetailsCustom,
                                   @ApiParam(name = "code", value = "验证码", required = false)
                                   @RequestParam(required = false) String code) {
        usersService.getSensitiveAccess(userDetailsCustom.getId(), userDetailsCustom.getPhone(), code);
        return Result.ok();
    }
}
