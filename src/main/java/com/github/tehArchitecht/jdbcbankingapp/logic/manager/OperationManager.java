package com.github.tehArchitecht.jdbcbankingapp.logic.manager;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Operation;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import com.github.tehArchitecht.jdbcbankingapp.logic.util.CurrencyConverter;
import com.github.tehArchitecht.jdbcbankingapp.logic.util.EntityMapper;
import com.github.tehArchitecht.jdbcbankingapp.logic.Result;
import com.github.tehArchitecht.jdbcbankingapp.logic.Status;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.DepositFundsRequest;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.TransferFundsRequest;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.response.OperationDto;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityManager;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityToken;
import com.github.tehArchitecht.jdbcbankingapp.logic.service.AccountService;
import com.github.tehArchitecht.jdbcbankingapp.logic.service.OperationService;
import com.github.tehArchitecht.jdbcbankingapp.logic.service.UserService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OperationManager extends ValidatingManager {
    private final SecurityManager securityManager;

    public OperationManager(SecurityManager securityManager) {
        this.securityManager = securityManager;
    }

    public Result<List<OperationDto>> getUserOperations(SecurityToken token) {
        try {
            if (securityManager.isTokenInvalid(token))
                return Result.ofFailure(Status.FAILURE_BAD_TOKEN);
            Long userId = securityManager.getUserId(token);

            List<Account> accounts = AccountService.getUserAccounts(userId);
            Stream<OperationDto> stream = Stream.empty();
            for (Account account : accounts) {
                UUID accountId = account.getId();
                List<Operation> operations = OperationService.findAllByAccountId(accountId);
                stream = Stream.concat(
                        stream,
                        operations.stream().map(op -> EntityMapper.convertOperation(op, accountId))
                );
            }

            return Result.ofSuccess(
                    Status.GET_USER_OPERATIONS_SUCCESS,
                    stream.sorted(Comparator.comparing(OperationDto::getDate)).collect(Collectors.toList())
            );
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    public Status depositFunds(SecurityToken token, DepositFundsRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Status.FAILURE_VALIDATION_ERROR;

        UUID accountId = request.getAccountId();
        BigDecimal amount = request.getAmount();
        Currency currency = request.getCurrency();

        try {
            Result<Account> result = getAccountEntity(token, accountId);
            if (result.failure())
                return result.getStatus();
            Account account = result.getData();

            BigDecimal converted = CurrencyConverter.convert(amount, currency, account.getCurrency());
            BigDecimal newBalance = account.getBalance().add(converted);
            AccountService.setBalance(account.getId(), newBalance);

            return Status.DEPOSIT_FUNDS_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Status transferFunds(SecurityToken token, TransferFundsRequest request) {
        if (request == null || hasConstraintViolations(request))
            return Status.FAILURE_VALIDATION_ERROR;

        UUID senderAccountId = request.getSenderAccountId();
        String receiverPhoneNumber = request.getReceiverPhoneNumber();
        BigDecimal amount = request.getAmount();
        Currency currency = request.getCurrency();

        try {
            Result<Account> result = getAccountEntity(token, senderAccountId);
            if (result.failure())
                return result.getStatus();
            Account senderAccount = result.getData();

            Optional<User> userOptional = UserService.getByPhoneNumber(receiverPhoneNumber);
            if (!userOptional.isPresent())
                return Status.TRANSFER_FUNDS_FAILURE_INVALID_PHONE_NUMBER;
            User receiver = userOptional.get();

            Optional<Account> accountOptional = AccountService.getUserPrimaryAccount(receiver.getId());
            if (!accountOptional.isPresent())
                return Status.TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_PRIMARY_ACCOUNT;
            Account receiverAccount = accountOptional.get();

            if (senderAccountId.equals(receiverAccount.getId()))
                return Status.TRANSFER_FUNDS_FAILURE_SAME_ACCOUNT;

            BigDecimal senderInitialBalance = senderAccount.getBalance();
            BigDecimal receiverInitialBalance = receiverAccount.getBalance();

            Currency senderCurrency = senderAccount.getCurrency();
            Currency receiverCurrency = receiverAccount.getCurrency();

            BigDecimal senderAmount = CurrencyConverter.convert(amount, currency, senderCurrency);
            BigDecimal receiverAmount = CurrencyConverter.convert(amount, currency, receiverCurrency);

            if (senderAccount.getBalance().compareTo(senderAmount) < 0)
                return Status.TRANSFER_FUNDS_FAILURE_INSUFFICIENT_FUNDS;

            BigDecimal senderResultingBalance = senderInitialBalance.subtract(senderAmount);
            BigDecimal receiverResultingBalance = receiverInitialBalance.add(receiverAmount);

            AccountService.setBalance(senderAccount.getId(), senderResultingBalance);
            AccountService.setBalance(receiverAccount.getId(), receiverResultingBalance);

            logTransfer(
                    senderAccount,
                    receiverAccount,
                    currency,
                    amount,
                    senderInitialBalance,
                    senderResultingBalance,
                    receiverInitialBalance,
                    receiverResultingBalance
            );

            return Status.TRANSFER_FUNDS_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Helper methods                                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    private static void logTransfer(Account sender, Account receiver, Currency currency, BigDecimal amount,
                                    BigDecimal senderInitialBalance, BigDecimal senderResultingBalance,
                                    BigDecimal receiverInitialBalance, BigDecimal receiverResultingBalance)
            throws DataAccessException {
        OperationService.add(new Operation(
                new Timestamp(System.currentTimeMillis()),
                currency,
                sender.getId(),
                receiver.getId(),
                amount,
                senderInitialBalance,
                senderResultingBalance,
                receiverInitialBalance,
                receiverResultingBalance
        ));
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
