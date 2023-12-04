package com.f4pl0.pinnacle.userservice.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {
    @Test
    void testAllArgsConstructorAndData() {
        UUID expectedId = UUID.randomUUID();
        String expectedUsername = "JohnDoe";
        String expectedEmail = "john.doe@example.com";
        String expectedPassword = "password123";
        Set<Authority> expectedAuthorities = new HashSet<>();
        String expectedFirstName = "John";
        String expectedLastName = "Doe";

        User user = new User();
        user.setId(expectedId);
        user.setUsername(expectedUsername);
        user.setEmail(expectedEmail);
        user.setPassword(expectedPassword);
        user.setAuthorities(expectedAuthorities);
        user.setFirstName(expectedFirstName);
        user.setLastName(expectedLastName);

        assertEquals(expectedId, user.getId());
        assertEquals(expectedUsername, user.getUsername());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(expectedPassword, user.getPassword());
        assertEquals(expectedAuthorities, user.getAuthorities());
        assertEquals(expectedFirstName, user.getFirstName());
        assertEquals(expectedLastName, user.getLastName());
    }
}
