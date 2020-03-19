package com.github.tehArchitecht.logic.service;

import com.github.tehArchitecht.data.DbAccessException;
import com.github.tehArchitecht.data.model.Account;
import com.github.tehArchitecht.data.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class AccountService {
    static boolean add(Account account) {
        try {
            return AccountRepository.save(account);
        } catch (DbAccessException e) {
            return false;
        }
    }

    static Optional<Boolean> exists(UUID accountId) {
        try {
            boolean exists = AccountRepository.existsById(accountId);
            return Optional.of(exists);
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }

    static Optional<Account> get(UUID accountId) {
        try {
            Account user = AccountRepository.findById(accountId);
            return (user == null) ? Optional.empty() : Optional.of(user);
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }

    static boolean setBalance(UUID accountId, BigDecimal balance) {
        try {
            return AccountRepository.setAccountBalanceById(accountId, balance);
        } catch (DbAccessException e) {
            return false;
        }
    }

    public static Optional<Integer> countUserAccounts(Long userId) {
        try {
            Integer count = AccountRepository.countByUserId(userId);
            return (count == null) ? Optional.empty() : Optional.of(count);
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }

    static Optional<List<Account>> getUserAccounts(Long userId) {
        try {
            List<Account> accounts = AccountRepository.findAllByUserId(userId);
            return (accounts == null) ? Optional.empty() : Optional.of(accounts);
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }

    static Optional<Account> getUserPrimaryAccount(Long userId) {
        try {
            List<Account> accounts = AccountRepository.findAllByUserId(userId);
            if (accounts == null) return Optional.empty();
            accounts.sort(Comparator.comparing(Account::getNumber));
            return Optional.of(accounts.get(0));
        } catch (DbAccessException e) {
            return Optional.empty();
        }
    }
}
