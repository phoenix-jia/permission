package com.famesmart.privilege.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.famesmart.privilege.entity.Privileges;
import com.famesmart.privilege.entity.bo.PrivilegeBO;
import com.famesmart.privilege.service.PrivilegesService;
import com.famesmart.privilege.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = "权限")
@RestController
@RequestMapping("/privileges")
public class PrivilegesController {

    @Autowired
    private PrivilegesService privilegesService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping(value = "/all")
    @ApiOperation(value = "全部权限获取", httpMethod = "GET")
    public Result list(@ApiParam(name = "pageNum", value = "第几页")
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @ApiParam(name = "pageSize", value = "每一页显示的条数")
                       @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<Privileges> aPage = privilegesService.page(new Page<>(pageNum, pageSize));
        return Result.ok(aPage);
    }

    @GetMapping(value = "/{id}")
    @ApiOperation(value = "权限获取", httpMethod = "GET")
    public Result getById(@ApiParam(name = "id", value = "权限id", required = true)
                                              @PathVariable String id) {
        return Result.ok(privilegesService.getById(id));
    }

    @PostMapping(value = "/")
    @ApiOperation(value = "权限添加", httpMethod = "POST")
    public Result add(@RequestBody @Valid PrivilegeBO privilegeBO,
                                      @ApiIgnore BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error("missing resource/operation");
        }
        Privileges privilege = modelMapper.map(privilegeBO, Privileges.class);
        privilegesService.save(privilege);
        return Result.ok(privilege);
    }


    @PutMapping(value = "/{id}")
    @ApiOperation(value = "权限更新", httpMethod = "PUT")
    public Result update(@ApiParam(name = "id", value = "权限id", required = true)
                                         @PathVariable Integer id,
                                         @RequestBody PrivilegeBO privilegeBO) {
        Privileges privilege = modelMapper.map(privilegeBO, Privileges.class);
        privilege.setId(id);
        privilegesService.updateById(privilege);
        return Result.ok();
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "权限删除", httpMethod = "DELETE")
    public Result delete(@ApiParam(name = "id", value = "权限id", required = true)
                         @PathVariable Integer id) {
        privilegesService.deleteById(id);
        return Result.ok();
    }

    @GetMapping("/resourceList")
    @ApiOperation(value = "全部资源获取", httpMethod = "GET")
    public Result getAllResource() {
        return Result.ok(privilegesService.selectAllResource());
    }

    @GetMapping("/resourceOperation")
    @ApiOperation(value = "资源操作获取", httpMethod = "GET")
    public Result getAllOperation(@ApiParam(name = "resource", value = "资源", required = true)
                                  @RequestParam String resource) {
        return Result.ok(privilegesService.selectByResource(resource));
    }
}
