package com.f4pl0.pinnacle.userservice.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthorityTest {

    @Test
    void testAllArgsConstructorAndData() {
        UUID expectedId = UUID.randomUUID();
        String expectedAuthority = "ROLE_USER";

        Authority authority = new Authority();
        authority.setId(expectedId);
        authority.setAuthority(expectedAuthority);

        assertEquals(expectedId, authority.getId());
        assertEquals(expectedAuthority, authority.getAuthority());
    }
}