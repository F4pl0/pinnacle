package com.f4pl0.pinnacle.userservice.service;

import com.f4pl0.pinnacle.userservice.dto.UserRegisterDTO;
import com.f4pl0.pinnacle.userservice.exception.UserRegistrationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void registerNewUserSuccessfully() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");

        ResponseEntity<String> response = restTemplate.postForEntity("/api/user/register", userRegisterDTO, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void registerUserWithExistingEmailThrowsException() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");

        restTemplate.postForEntity("/api/user/register", userRegisterDTO, String.class);

        ResponseEntity<UserRegistrationException> response = restTemplate.postForEntity("/api/user/register", userRegisterDTO, UserRegistrationException.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void registerUserWithEmptyPassword() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("");
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");

        ResponseEntity<UserRegistrationException> response = restTemplate.postForEntity("/api/user/register", userRegisterDTO, UserRegistrationException.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserWithNullPassword() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword(null);
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");

        ResponseEntity<UserRegistrationException> response = restTemplate.postForEntity("/api/user/register", userRegisterDTO, UserRegistrationException.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserWithEmptyFirstName() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("");
        userRegisterDTO.setLastName("User");

        ResponseEntity<UserRegistrationException> response = restTemplate.postForEntity("/api/user/register", userRegisterDTO, UserRegistrationException.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserWithNullFirstName() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName(null);
        userRegisterDTO.setLastName("User");

        ResponseEntity<UserRegistrationException> response = restTemplate.postForEntity("/api/user/register", userRegisterDTO, UserRegistrationException.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserWithEmptyLastName() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("");

        ResponseEntity<UserRegistrationException> response = restTemplate.postForEntity("/api/user/register", userRegisterDTO, UserRegistrationException.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void registerUserWithNullLastName() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName(null);

        ResponseEntity<UserRegistrationException> response = restTemplate.postForEntity("/api/user/register", userRegisterDTO, UserRegistrationException.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}