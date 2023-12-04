package com.f4pl0.pinnacle.userservice.service;

import com.f4pl0.pinnacle.userservice.dto.UserRegisterDTO;
import com.f4pl0.pinnacle.userservice.exception.UserRegistrationException;
import com.f4pl0.pinnacle.userservice.model.Authority;
import com.f4pl0.pinnacle.userservice.model.User;
import com.f4pl0.pinnacle.userservice.repository.AuthorityRepository;
import com.f4pl0.pinnacle.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private AuthorityRepository authorityRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerNewUserSuccessfully() throws UserRegistrationException {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authorityRepository.findByAuthority(anyString())).thenReturn(new Authority());

        userService.register(userRegisterDTO);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUserWithExistingEmailThrowsException() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setEmail("test@test.com");
        userRegisterDTO.setPassword("password");
        userRegisterDTO.setFirstName("Test");
        userRegisterDTO.setLastName("User");

        when(userRepository.findByEmail(anyString())).thenReturn(new User());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(authorityRepository.findByAuthority(anyString())).thenReturn(new Authority());

        assertThrows(UserRegistrationException.class, () -> userService.register(userRegisterDTO));
    }
}