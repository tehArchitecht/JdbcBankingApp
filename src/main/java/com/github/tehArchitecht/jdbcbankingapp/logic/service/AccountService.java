package com.github.tehArchitecht.jdbcbankingapp.logic.service;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.data.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

class AccountService {
    static void add(Account account) throws DataAccessException {
        AccountRepository.save(account);
    }

    static Optional<Account> get(UUID accountId) throws DataAccessException {
        return AccountRepository.findById(accountId);
    }

    static void setBalance(UUID accountId, BigDecimal balance) throws DataAccessException {
        AccountRepository.setAccountBalanceById(accountId, balance);
    }

    static List<Account> getUserAccounts(Long userId) throws DataAccessException {
        return AccountRepository.findAllByUserId(userId);
    }

    static int countUserAccounts(Long userId) throws DataAccessException {
        return AccountRepository.countByUserId(userId);
    }

    static Optional<Account> getUserPrimaryAccount(Long userId) throws DataAccessException {
        List<Account> accounts = AccountRepository.findAllByUserId(userId);
        return accounts.stream().min(Comparator.comparing(Account::getNumber));
    }
}
