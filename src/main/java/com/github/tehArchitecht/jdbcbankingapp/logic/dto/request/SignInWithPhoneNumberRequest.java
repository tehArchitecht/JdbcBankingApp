package com.github.tehArchitecht.jdbcbankingapp.logic.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class SignInWithPhoneNumberRequest {
    private String phoneNumber;
    private String password;
}
