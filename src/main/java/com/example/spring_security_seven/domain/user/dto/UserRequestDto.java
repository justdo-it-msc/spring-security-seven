package com.example.spring_security_seven.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

    private String username;

    private String password;
}
