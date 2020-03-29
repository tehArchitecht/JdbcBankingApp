package com.github.tehArchitecht.jdbcbankingapp.logic.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {
    private String userName;
    private String password;
    private String address;
    private String phoneNumber;
}
