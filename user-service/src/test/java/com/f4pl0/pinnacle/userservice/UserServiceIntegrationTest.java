package com.f4pl0.pinnacle.userservice;

import com.f4pl0.pinnacle.userservice.dto.UserRegisterDTO;
import com.f4pl0.pinnacle.userservice.exception.UserRegistrationException;
import com.f4pl0.pinnacle.userservice.model.User;
import com.f4pl0.pinnacle.userservice.repository.AuthorityRepository;
import com.f4pl0.pinnacle.userservice.repository.UserRepository;
import com.f4pl0.pinnacle.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void testRegisterNewUser() throws UserRegistrationException {
        // Given
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@example.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("John");
        userRegisterDTO.setLastName("Doe");

        // When
        userService.register(userRegisterDTO);

        // Then
        User savedUser = userRepository.findByEmail(userRegisterDTO.getEmail());
        assertNotNull(savedUser);
        assertEquals(userRegisterDTO.getEmail(), savedUser.getEmail());
        assertTrue(passwordEncoder.matches(userRegisterDTO.getPassword(), savedUser.getPassword()));
    }

    @Test
    public void testRegisterUserWithDuplicateEmail() {
        // Given
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("existing@example.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("John");
        userRegisterDTO.setLastName("Doe");

        // Ensure an existing user with the same email
        User existingUser = new User();
        existingUser.setEmail(userRegisterDTO.getEmail());
        userRepository.save(existingUser);

        // When and Then
        assertThrows(UserRegistrationException.class, () -> userService.register(userRegisterDTO));
    }
}