package com.f4pl0.pinnacle.userservice.repository;

import com.f4pl0.pinnacle.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);
    User findByUsername(String username);

}
