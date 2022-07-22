package com.example.keycloak.controller;

import com.example.keycloak.repo.UserRepository;
import common.domain.base.ApiResponse;
import common.domain.entity.Users;
import common.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/keycloak")
public class TestController {
    private final Environment env;
    private final UserRepository userRepo;

    public TestController(Environment env, UserRepository userRepo) {
        this.env = env;
        this.userRepo = userRepo;
    }

    @GetMapping("")
    public ApiResponse<?> getApplicationName() {
        userRepo.save(new Users());
        String a = null;
        if (a == null) {
            throw ApiException.builder()
                    .code("200")
                    .build();

        }

        return ApiResponse.ok(env.getProperty("spring.application.name"));
    }
}
