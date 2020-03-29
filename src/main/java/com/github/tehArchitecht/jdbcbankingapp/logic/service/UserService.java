package com.github.tehArchitecht.jdbcbankingapp.logic.service;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import com.github.tehArchitecht.jdbcbankingapp.data.repository.AccountRepository;
import com.github.tehArchitecht.jdbcbankingapp.data.repository.UserRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

class UserService {
    static void add(User user) throws DataAccessException {
        UserRepository.save(user);
    }

    static boolean isNameInUse(String name) throws DataAccessException {
        return UserRepository.existsByName(name);
    }

    static boolean isPhoneNumberInUse(String phoneNumber) throws DataAccessException {
        return UserRepository.existsByPhoneNumber(phoneNumber);
    }

    static Optional<User> getByName(String name) throws DataAccessException {
        return UserRepository.findByName(name);
    }

    static Optional<User> getByPhoneNumber(String phoneNumber) throws DataAccessException {
        return UserRepository.findByPhoneNumber(phoneNumber);
    }

    static void setPrimaryAccountId(Long userId, UUID primaryAccountId) throws DataAccessException {
        UserRepository.setPrimaryAccountIdById(userId, primaryAccountId);
    }
}
