package com.example.keycloak.controller;

import com.example.keycloak.repo.UserRepository;
import common.domain.base.ApiResponse;
import common.domain.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keycloak")
@RequiredArgsConstructor
public class TestController {
    private final Environment env;
    private final UserRepository userRepo;

    @GetMapping("")
    public ApiResponse<?> getApplicationName() {
        userRepo.save(new Users());
        return ApiResponse.ok(env.getProperty("spring.application.name"));
    }
}
