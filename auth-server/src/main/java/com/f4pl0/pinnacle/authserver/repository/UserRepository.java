package com.f4pl0.pinnacle.authserver.repository;

import com.f4pl0.pinnacle.authserver.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    User findByEmail(String email);
    User findByUsername(String username);

}
