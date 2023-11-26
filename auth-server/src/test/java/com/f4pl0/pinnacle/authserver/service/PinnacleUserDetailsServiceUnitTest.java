package com.f4pl0.pinnacle.authserver.service;

import com.f4pl0.pinnacle.authserver.model.PinnacleUserPrincipal;
import com.f4pl0.pinnacle.authserver.model.User;
import com.f4pl0.pinnacle.authserver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class PinnacleUserDetailsServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PinnacleUserDetailsService serviceUnderTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        serviceUnderTest = new PinnacleUserDetailsService(userRepository);
    }

    @Test
    public void loadUserByUsername_whenUserExists_returnsUserDetails() {
        User mockUser = new User();
        mockUser.setUsername("testUser");
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser);

        PinnacleUserPrincipal result = (PinnacleUserPrincipal) serviceUnderTest.loadUserByUsername("testUser");

        assertEquals(mockUser.getUsername(), result.getUsername());
    }

    @Test
    public void loadUserByUsername_whenUserDoesNotExist_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, () -> serviceUnderTest.loadUserByUsername("nonExistentUser"));
    }

    @Test
    public void loadUserByUsername_whenUsernameIsNull_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> serviceUnderTest.loadUserByUsername(null));
    }

    @Test
    public void loadUserByUsername_whenUsernameIsEmpty_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> serviceUnderTest.loadUserByUsername(""));
    }

    @Test
    public void loadUserByUsername_whenUserExists_returnsCorrectUserDetails() {
        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPassword("testPassword");
        when(userRepository.findByUsername(anyString())).thenReturn(mockUser);

        PinnacleUserPrincipal result = (PinnacleUserPrincipal) serviceUnderTest.loadUserByUsername("testUser");

        assertEquals(mockUser.getUsername(), result.getUsername());
        assertEquals(mockUser.getPassword(), result.getPassword());
    }
}
