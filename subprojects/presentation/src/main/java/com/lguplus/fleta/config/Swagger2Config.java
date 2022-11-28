package com.lguplus.fleta.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    private static final String API_CLASSIFICATION = "MIMS";
    private static final String API_VERSION = "1.0.0";
    private static final String API_DESCRIPTION = "API 명세서(스펙)";

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * localhost:8080/swagger-ui.html
     *
     * @return new Docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .consumes(this.getConsumeContentTypes())
            .produces(this.getProduceContentTypes())
            .apiInfo(this.apiInfo())
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.lguplus.fleta.api"))
            .paths(PathSelectors.any())
            .build();
    }

    private Set<String> getConsumeContentTypes() {
        Set<String> consumes = new HashSet<>();
        consumes.add("application/json;charset=UTF-8");
        consumes.add("application/xml;charset=UTF-8");
        consumes.add(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        return consumes;
    }

    private Set<String> getProduceContentTypes() {
        Set<String> produces = new HashSet<>();
        produces.add(MediaType.APPLICATION_JSON_VALUE);
        produces.add(MediaType.APPLICATION_XML_VALUE);
        return produces;
    }

    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(applicationName)
                .version(API_VERSION)
                .description(API_CLASSIFICATION + ": " + applicationName + " 서비스 " + API_DESCRIPTION)
                .build();
    }
}
