package com.f4pl0.pinnacle.userservice;

import com.f4pl0.pinnacle.userservice.repository.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest()
@ContextConfiguration(classes = H2DatabaseTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceApplicationTest {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationContext context;

    @Test
    void injectedComponentsAreNotNull() {
        assert dataSource != null;
        assert jdbcTemplate != null;
        assert entityManager != null;
        assert userRepository != null;
    }

    @Test
    void contextLoads() {
    }

    @Test
    void mainStartsApplication() {
        System.setProperty("spring.profiles.active", "test");
        UserServiceApplication.main(new String[] {});
        assertThat(context).isNotNull();
    }
}