package com.f4pl0.pinnacle.userservice.repository;

import com.f4pl0.pinnacle.userservice.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Authority findByAuthority(String authority);
}
