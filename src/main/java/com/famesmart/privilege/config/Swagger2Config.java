package com.famesmart.privilege.config;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@EnableSwagger2
public class Swagger2Config implements EnvironmentAware {

    private Environment environment;

    @Bean
    public Docket api() {
        String env =  environment.getProperty("NODE_ENV");
        boolean enable = env == null || env.equals("prerelease");

        List<Parameter> parameters = new ArrayList<>();
        parameters.add(new ParameterBuilder()
                .name("token")
                .description("认证token")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build());

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .enable(enable)
                .globalOperationParameters(parameters)
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.famesmart.privilege.controller"))
                .paths(PathSelectors.any())
                .build();
    }

     private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("权限管理api")
                .version("1.0.0")   // 文档版本号
                .build();
    }

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }
}
