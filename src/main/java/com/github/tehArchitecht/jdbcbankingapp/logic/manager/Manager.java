package com.github.tehArchitecht.jdbcbankingapp.logic.manager;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.logic.Result;
import com.github.tehArchitecht.jdbcbankingapp.logic.Status;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityManager;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityToken;
import com.github.tehArchitecht.jdbcbankingapp.service.AccountService;

import java.util.Optional;
import java.util.UUID;

abstract class Manager {
    protected final SecurityManager securityManager;

    protected Manager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    protected Result<Account> getAccountEntity(SecurityToken token, UUID accountId) {
        try {
            if (securityManager.isTokenInvalid(token))
                return Result.ofFailure(Status.FAILURE_BAD_TOKEN);
            Long userId = securityManager.getUserId(token);

            Optional<Account> optional = AccountService.get(accountId);
            if (!optional.isPresent())
                return Result.ofFailure(Status.FAILURE_INVALID_ACCOUNT_ID);

            Account account = optional.get();
            if (!account.getUserId().equals(userId))
                return Result.ofFailure(Status.FAILURE_UNAUTHORIZED_ACCESS);

            return Result.ofSuccess(null, account);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }
}
