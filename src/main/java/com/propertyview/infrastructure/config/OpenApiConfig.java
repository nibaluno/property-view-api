package com.propertyview.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {
    @Bean
    OpenAPI propertyViewOpenApi() {
        return new OpenAPI().info(new Info()
            .title("Property View API")
            .version("1.0.0")
            .description("RESTful API for managing hotels: create, read, search and aggregate."));
    }
}
