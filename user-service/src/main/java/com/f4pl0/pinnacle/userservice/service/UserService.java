package com.f4pl0.pinnacle.userservice.service;

import com.f4pl0.pinnacle.userservice.dto.UserRegisterDTO;
import com.f4pl0.pinnacle.userservice.exception.UserRegistrationException;
import com.f4pl0.pinnacle.userservice.model.Authority;
import com.f4pl0.pinnacle.userservice.model.User;
import com.f4pl0.pinnacle.userservice.repository.AuthorityRepository;
import com.f4pl0.pinnacle.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(UserRegisterDTO userRegisterDTO) throws UserRegistrationException {
        if (userRepository.findByEmail(userRegisterDTO.getEmail()) != null) {
            throw new UserRegistrationException("Email is already registered");
        }

        User user = new User();
        user.setUsername(userRegisterDTO.getEmail());
        user.setEmail(userRegisterDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userRegisterDTO.getPassword()));
        user.setFirstName(userRegisterDTO.getFirstName());
        user.setLastName(userRegisterDTO.getLastName());

        // Assign the ROLE_USER authority to the user
        Authority userAuthority = authorityRepository.findByAuthority("ROLE_USER");
        user.setAuthorities(Collections.singleton(userAuthority));

        userRepository.save(user);
    }
}
