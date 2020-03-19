package com.github.tehArchitecht.logic.service;

import com.github.tehArchitecht.data.DbAccessException;
import com.github.tehArchitecht.data.model.User;
import com.github.tehArchitecht.data.repository.UserRepository;
import java.util.Optional;

class UserService {
    static boolean add(User user) {
        try {
            return UserRepository.save(user);
        } catch (DbAccessException e) {
            return false;
        }
    }

    static Optional<Boolean> isNameInUse(String name) {
        try {
            return Optional.of(UserRepository.existsByName(name));
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }

    static Optional<Boolean> isPhoneNumberInUse(String phoneNumber) {
        try {
            return Optional.of(UserRepository.existsByPhoneNumber(phoneNumber));
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }

    static Optional<User> getByName(String name) {
        try {
            User user = UserRepository.findByName(name);
            return (user == null) ? Optional.empty() : Optional.of(user);
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }

    static Optional<User> getByPhoneNumber(String phoneNumber) {
        try {
            User user = UserRepository.findByPhoneNumber(phoneNumber);
            return (user == null) ? Optional.empty() : Optional.of(user);
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }
}
