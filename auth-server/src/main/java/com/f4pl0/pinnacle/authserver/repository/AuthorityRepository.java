package com.f4pl0.pinnacle.authserver.repository;

import com.f4pl0.pinnacle.authserver.model.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorityRepository extends JpaRepository<Authority, UUID> {
    Authority findByAuthority(String authority);
}
