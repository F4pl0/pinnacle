package com.f4pl0.pinnacle.authserver.model;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class PinnacleUserPrincipalTest {

        @Test
        void testPinnacleUserPrincipal() {
            Authority userRole = new Authority();
            userRole.setAuthority("ROLE_USER");

            User user = new User();
            user.setUsername("test");
            user.setPassword("test");
            user.setAuthorities(Collections.singleton(userRole));

            PinnacleUserPrincipal pinnacleUserPrincipal = new PinnacleUserPrincipal(user);

            assertEquals("test", pinnacleUserPrincipal.getUsername());
            assertEquals("test", pinnacleUserPrincipal.getPassword());
            assertEquals(1, pinnacleUserPrincipal.getAuthorities().size());
            assertEquals(new SimpleGrantedAuthority("ROLE_USER"), pinnacleUserPrincipal.getAuthorities().iterator().next());
            assertNotNull(pinnacleUserPrincipal.toString());
            assertTrue(pinnacleUserPrincipal.isAccountNonExpired());
            assertTrue(pinnacleUserPrincipal.isAccountNonLocked());
            assertTrue(pinnacleUserPrincipal.isCredentialsNonExpired());
            assertTrue(pinnacleUserPrincipal.isEnabled());
        }

}