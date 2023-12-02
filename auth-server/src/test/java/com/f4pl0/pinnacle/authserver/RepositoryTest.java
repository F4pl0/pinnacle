package com.f4pl0.pinnacle.authserver;

import com.f4pl0.pinnacle.authserver.model.Authority;
import com.f4pl0.pinnacle.authserver.model.User;
import com.f4pl0.pinnacle.authserver.repository.AuthorityRepository;
import com.f4pl0.pinnacle.authserver.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositoryTests {

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

        userRepository.save(user);

        Optional<User> foundUser = Optional.ofNullable(userRepository.findByUsername("test@email.com"));

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("test@email.com");
        assertThat(foundUser.get().getPassword()).isEqualTo("testPassword");
    }

    @Test
    void testAuthorityRepository() {
        Authority authority = new Authority();
        authority.setAuthority("ROLE_USER");

        authorityRepository.save(authority);

        Optional<Authority> foundAuthority = Optional.ofNullable(authorityRepository.findByAuthority("ROLE_USER"));

        assertThat(foundAuthority).isPresent();
        assertThat(foundAuthority.get().getAuthority()).isEqualTo("ROLE_USER");
    }
}