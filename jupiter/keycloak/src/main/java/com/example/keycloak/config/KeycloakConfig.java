package com.example.keycloak.config;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfig {
    @Autowired
    private Environment env;

    public Keycloak getKeycloakInstance() {
        return Keycloak.getInstance(
                env.getProperty("keycloak.auth-server-url"),
                env.getProperty("keycloak.realm"),
                env.getProperty("keycloak.resource"),
                env.getProperty("keycloak-client.client-id"),
                env.getProperty("keycloak-client.client-secret")
        );
    }

}
