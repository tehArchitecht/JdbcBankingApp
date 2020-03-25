package com.github.tehArchitecht.jdbcbankingapp.data.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    private Long id;
    private String name;
    private String password;
    private String address;
    private String phoneNumber;

    public User(String name, String password, String address, String phoneNumber) {
        this.name = name;
        this.password = password;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }
}