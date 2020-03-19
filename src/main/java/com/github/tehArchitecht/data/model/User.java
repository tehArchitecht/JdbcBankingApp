package com.github.tehArchitecht.data.model;

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
}