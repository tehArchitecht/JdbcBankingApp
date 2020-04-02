package com.github.tehArchitecht.jdbcbankingapp.service;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import com.github.tehArchitecht.jdbcbankingapp.data.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class UserService {
    public static void add(User user) throws DataAccessException {
        UserRepository.save(user);
    }

    public static boolean isNameInUse(String name) throws DataAccessException {
        return UserRepository.existsByName(name);
    }

    public static boolean isPhoneNumberInUse(String phoneNumber) throws DataAccessException {
        return UserRepository.existsByPhoneNumber(phoneNumber);
    }

    public static Optional<User> getByName(String name) throws DataAccessException {
        return UserRepository.findByName(name);
    }

    public static Optional<User> getByPhoneNumber(String phoneNumber) throws DataAccessException {
        return UserRepository.findByPhoneNumber(phoneNumber);
    }

    public static void setPrimaryAccountId(Long userId, UUID primaryAccountId) throws DataAccessException {
        UserRepository.setPrimaryAccountIdById(userId, primaryAccountId);
    }
}
