package com.github.tehArchitecht.jdbcbankingapp.logic;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Operation;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.SignUpRequest;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.response.AccountDto;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.response.OperationDto;

import java.util.UUID;

public class EntityMapper {
    public static User extractUser(SignUpRequest request) {
        return new User(
                request.getUserName(),
                request.getPassword(),
                request.getAddress(),
                request.getPhoneNumber()
        );
    }

    public static AccountDto convertAccount(Account account, UUID primaryAccountId) {
        return new AccountDto(
                account.getId(),
                account.getBalance(),
                account.getCurrency(),
                account.getId().equals(primaryAccountId)
        );
    }

    public static OperationDto convertOperation(Operation operation, UUID accountId) {
        if (accountId.equals(operation.getSenderAccountId())) {
            return new OperationDto(
                    operation.getDate(),
                    operation.getCurrency(),
                    operation.getSenderAccountId(),
                    operation.getReceiverAccountId(),
                    operation.getAmount(),
                    operation.getSenderInitialBalance(),
                    operation.getSenderResultingBalance()
            );
        } else {
            return new OperationDto(
                    operation.getDate(),
                    operation.getCurrency(),
                    operation.getSenderAccountId(),
                    operation.getReceiverAccountId(),
                    operation.getAmount(),
                    operation.getReceiverInitialBalance(),
                    operation.getReceiverResultingBalance()
            );
        }
    }
}
