package com.f4pl0.pinnacle.userservice.controller;

import com.f4pl0.pinnacle.userservice.dto.UserRegisterDTO;
import com.f4pl0.pinnacle.userservice.exception.UserRegistrationException;
import com.f4pl0.pinnacle.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class UserController {
    private UserService userService;

    @PostMapping("/api/user/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody UserRegisterDTO userRegisterDTO) throws UserRegistrationException {
        userService.register(userRegisterDTO);
    }
}
