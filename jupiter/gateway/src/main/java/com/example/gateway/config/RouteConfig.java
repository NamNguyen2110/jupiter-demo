package com.example.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("users-route", r -> r.path("/*/users/**").uri("http://localhost:8082"))
                .route("keycloak-route", r -> r.path("/*/keycloak/**").uri("http://localhost:8100"))
                .build();
    }

}
