package com.github.tehArchitecht.jdbcbankingapp.data.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String password;
    private String address;
    private String phoneNumber;
    private UUID primaryAccountId;

    public User(String name, String password, String address, String phoneNumber) {
        this.name = name;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.primaryAccountId = null;
    }
}