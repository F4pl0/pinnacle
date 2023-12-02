package com.f4pl0.pinnacle.authserver.repository;

import com.f4pl0.pinnacle.authserver.model.Authority;
import com.f4pl0.pinnacle.authserver.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DataJpaTest
class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Test
    void testUserRepository() {
        User user = new User();
        user.setUsername("test@email.com");
        user.setEmail("test@email.com");
        user.setPassword("testPassword");
        user.setFirstName("testFirstName");
        user.setLastName("testLastName");

        userRepository.save(user);

        Optional<User> foundUser = Optional.ofNullable(userRepository.findByUsername(user.getUsername()));

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
        assertThat(foundUser.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(foundUser.get().getPassword()).isEqualTo(user.getPassword());
        assertThat(foundUser.get().getFirstName()).isEqualTo(user.getFirstName());
        assertThat(foundUser.get().getLastName()).isEqualTo(user.getLastName());
        assertDoesNotThrow(() -> UUID.fromString(foundUser.get().getId().toString()));
    }

    @Test
    void testAuthorityRepository() {
        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");

        authorityRepository.save(authority);

        Optional<Authority> foundAuthority = Optional.ofNullable(authorityRepository.findByAuthority("ROLE_USER"));

        assertThat(foundAuthority).isPresent();
        assertThat(foundAuthority.get().getAuthority()).isEqualTo("ROLE_USER");
        assertDoesNotThrow(() -> UUID.fromString(foundAuthority.get().getId().toString()));
    }


}