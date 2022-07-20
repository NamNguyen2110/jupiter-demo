package com.example.keycloak.repo;

import common.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<Users, String> {
}
