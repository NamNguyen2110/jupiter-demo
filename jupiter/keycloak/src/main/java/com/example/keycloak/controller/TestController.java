package com.example.keycloak.controller;

import com.example.keycloak.config.KeycloakConfig;
import common.domain.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.Response;

@RestController
@RequestMapping("/keycloak")
@RequiredArgsConstructor
public class TestController {
    private final Environment env;

    @Autowired
    KeycloakConfig keycloakConfig;
    @Autowired
    Keycloak keycloak;

    @GetMapping("/test")
    public ApiResponse<?> getApplicationName() {
        UsersResource usersResource  = keycloak.realm(env.getProperty("keycloak.realm")).users();
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("randomUser");
        Response response = usersResource.create(userRepresentation);
        String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        UserResource userResource = usersResource.get(userId);

        return ApiResponse.ok(env.getProperty("spring.application.name"));
    }

}
