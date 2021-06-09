package com.famesmart.privilege.controller;

import com.famesmart.privilege.entity.bo.RoleBO;
import com.famesmart.privilege.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.famesmart.privilege.service.RolesService;
import com.famesmart.privilege.entity.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = "角色")
@RestController
@RequestMapping("/roles")
public class RolesController {


    @Autowired
    private RolesService rolesService;

    @GetMapping(value = "/all")
    @ApiOperation(value = "所有角色获取", httpMethod = "GET")
    public Result list() {
        return Result.ok(rolesService.list());
    }

    @GetMapping
    @ApiOperation(value = "角色查询", httpMethod = "GET")
    public Result get(@ApiParam(name = "id", value = "角色ID")
                       @RequestParam(required = false) Integer id,
                       @ApiParam(name = "name", value = "角色名称")
                       @RequestParam(required = false) String name) {
        if (id == null && StringUtils.isBlank(name)) {
            return Result.error("missing id/name");
        }

        return Result.ok(rolesService.getRoleVOByIdOrName(id, name));
    }

    @PostMapping
    @ApiOperation(value = "角色添加", httpMethod = "POST")
    public Result addRole(@RequestBody @Valid RoleBO roleBO,
                          @ApiIgnore BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error("missing name");
        }
        return Result.ok(rolesService.addRole(roleBO));
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "角色更新", httpMethod = "PUT")
    public Result updateRole(@ApiParam(name = "id", value = "角色id", required = true)
                             @PathVariable Integer id, @RequestBody RoleBO roleBO) {
        rolesService.updateRole(id, roleBO);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "角色删除", httpMethod = "DELETE")
    public Result deleteRole(@ApiParam(name = "id", value = "角色id", required = true)
                             @PathVariable Integer id) {
        rolesService.deleteRole(id);
        return Result.ok();
    }

}
