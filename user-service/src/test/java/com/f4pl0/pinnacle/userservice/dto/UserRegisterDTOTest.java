package com.f4pl0.pinnacle.userservice.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserRegisterDTOTest {
    @Test
    public void testAllArgsConstructorAndData() {
        String expectedFirstName = "John";
        String expectedLastName = "Doe";
        String expectedEmail = "john.doe@example.com";
        String expectedPassword = "password123";

        UserRegisterDTO user = new UserRegisterDTO();
        user.setFirstName(expectedFirstName);
        user.setLastName(expectedLastName);
        user.setEmail(expectedEmail);
        user.setPassword(expectedPassword);

        assertEquals(expectedFirstName, user.getFirstName());
        assertEquals(expectedLastName, user.getLastName());
        assertEquals(expectedEmail, user.getEmail());
        assertEquals(expectedPassword, user.getPassword());
    }
}
