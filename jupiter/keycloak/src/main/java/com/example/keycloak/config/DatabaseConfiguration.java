package com.example.keycloak.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan("common.domain.entity")
public class DatabaseConfiguration {
}
