package com.famesmart.privilege.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
public class AppController {

    @ApiIgnore
    @GetMapping("/health_check")
    public String healthCheck(){
        return "ok";
    }

    @ApiIgnore
    @GetMapping("/version")
    public String version(){
        return "v1.0";
    }

}
