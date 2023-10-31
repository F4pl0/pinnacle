package com.f4pl0.pinnacle.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
