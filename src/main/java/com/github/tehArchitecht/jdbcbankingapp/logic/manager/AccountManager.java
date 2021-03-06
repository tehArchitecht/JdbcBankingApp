package com.github.tehArchitecht.jdbcbankingapp.logic.manager;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;
import com.github.tehArchitecht.jdbcbankingapp.logic.util.EntityMapper;
import com.github.tehArchitecht.jdbcbankingapp.logic.Result;
import com.github.tehArchitecht.jdbcbankingapp.logic.Status;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.CreateAccountRequest;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.SetPrimaryAccountRequest;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.response.AccountDto;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityManager;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityToken;
import com.github.tehArchitecht.jdbcbankingapp.logic.service.AccountService;
import com.github.tehArchitecht.jdbcbankingapp.logic.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class AccountManager extends ValidatingManager {
    private final SecurityManager securityManager;

    public AccountManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public Result<List<AccountDto>> getUserAccounts(SecurityToken token) {
        try {
            if (securityManager.isTokenInvalid(token))
                return Result.ofFailure(Status.FAILURE_BAD_TOKEN);
            Long userId = securityManager.getUserId(token);

            Optional<Account> optional = AccountService.getUserPrimaryAccount(userId);
            if (!optional.isPresent())
                return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
            UUID primaryAccountId = optional.get().getId();

            List<Account> accounts = AccountService.getUserAccounts(userId);
            List<AccountDto> accountDtos = accounts.stream()
                    .map((Account account) -> EntityMapper.convertAccount(account, primaryAccountId))
                    .collect(Collectors.toList());
            return Result.ofSuccess(Status.GET_USER_ACCOUNTS_SUCCESS, accountDtos);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    public Status createAccount(SecurityToken token, CreateAccountRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Status.FAILURE_VALIDATION_ERROR;

        Currency currency = request.getCurrency();

        try {
            if (securityManager.isTokenInvalid(token))
                return Status.FAILURE_BAD_TOKEN;
            Long userId = securityManager.getUserId(token);

            AccountService.add(new Account(userId, currency));
            return Status.CREATE_ACCOUNT_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Status setPrimaryAccount(SecurityToken token, SetPrimaryAccountRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Status.FAILURE_VALIDATION_ERROR;

        UUID accountId = request.getAccountId();

        try {
            if (securityManager.isTokenInvalid(token))
                return Status.FAILURE_BAD_TOKEN;
            Long userId = securityManager.getUserId(token);

            Result<Boolean> result = canAccessAccount(userId, accountId);
            if (result.failure())
                return result.getStatus();

            UserService.setPrimaryAccountId(userId, accountId);
            return Status.SET_PRIMARY_ACCOUNT_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    private Result<Boolean> canAccessAccount(Long userId, UUID accountId) {
        try {
            Optional<Account> optional = AccountService.get(accountId);
            if (!optional.isPresent())
                return Result.ofFailure(Status.FAILURE_INVALID_ACCOUNT_ID);

            Account account = optional.get();
            if (!account.getUserId().equals(userId))
                return Result.ofFailure(Status.FAILURE_UNAUTHORIZED_ACCESS);

            return Result.ofSuccess(null, true);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }
}
