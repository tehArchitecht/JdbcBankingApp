package com.github.tehArchitecht.jdbcbankingapp.service;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import com.github.tehArchitecht.jdbcbankingapp.data.repository.AccountRepository;
import com.github.tehArchitecht.jdbcbankingapp.data.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AccountService {
    public static void add(Account account) throws DataAccessException {
        Account saved = AccountRepository.save(account);

        Optional<User> optional = UserRepository.findById(account.getUserId());
        if (!optional.isPresent())
            throw new DataAccessException();

        User user = optional.get();
        if (user.getPrimaryAccountId() == null) {
            UserRepository.setPrimaryAccountIdById(user.getId(), saved.getId());
        }
    }

    public static Optional<Account> get(UUID accountId) throws DataAccessException {
        return AccountRepository.findById(accountId);
    }

    public static void setBalance(UUID accountId, BigDecimal balance) throws DataAccessException {
        AccountRepository.setAccountBalanceById(accountId, balance);
    }

    public static List<Account> getUserAccounts(Long userId) throws DataAccessException {
        return AccountRepository.findAllByUserId(userId);
    }

    public static Optional<Account> getUserPrimaryAccount(Long userId) throws DataAccessException {
        Optional<User> optional = UserRepository.findById(userId);
        return optional.isPresent()
                ? AccountRepository.findById(optional.get().getPrimaryAccountId())
                : Optional.empty();
    }
}
