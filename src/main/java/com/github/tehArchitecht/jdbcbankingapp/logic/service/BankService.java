package com.github.tehArchitecht.jdbcbankingapp.logic.service;

import com.github.tehArchitecht.jdbcbankingapp.data.exception.DataAccessException;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Account;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;
import com.github.tehArchitecht.jdbcbankingapp.data.model.Operation;
import com.github.tehArchitecht.jdbcbankingapp.data.model.User;
import com.github.tehArchitecht.jdbcbankingapp.logic.CurrencyConverter;
import com.github.tehArchitecht.jdbcbankingapp.logic.Result;
import com.github.tehArchitecht.jdbcbankingapp.logic.Status;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.*;
import com.github.tehArchitecht.jdbcbankingapp.logic.security.SecurityManager;
import com.github.tehArchitecht.jdbcbankingapp.logic.security.SecurityToken;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BankService {
    private final SecurityManager securityManager;

    public BankService() {
        this.securityManager = new SecurityManager();
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Registration and authorisation operations                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

    public Result<Boolean> isNameInUse(String name) {
        try {
            return Result.ofSuccess(Status.IS_NAME_IN_USE_SUCCESS, UserService.isNameInUse(name));
        } catch (DataAccessException e) {
            return  Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    public Status signUp (String name, String password, String address, String phoneNumber) {
        try {
            if (UserService.isNameInUse(name) || UserService.isPhoneNumberInUse(phoneNumber))
                return Status.SING_UP_FAILURE_NAME_OR_PHONE_NUMBER_TAKEN;

            UserService.add(new User(name, password, address, phoneNumber));
            return Status.SIGN_UP_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Result<SecurityToken> signInWithName(String userName, String password) {
        try {
            return signIn(
                    UserService.getByName(userName),
                    password,
                    Status.SIGN_IN_WITH_NAME_SUCCESS,
                    Status.SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA
            );
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    public Result<SecurityToken> signWithPhoneNumber(String phoneNumber, String password) {
        try {
            return signIn(
                    UserService.getByPhoneNumber(phoneNumber),
                    password,
                    Status.SIGN_IN_WITH_PHONE_NUMBER_SUCCESS,
                    Status.SIGN_IN_WITH_PHONE_NUMBER_FAILURE_WRONG_DATA
            );
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Result<SecurityToken> signIn(Optional<User> optional, String password, Status success, Status failure) {
        if (!optional.isPresent())
            return Result.ofFailure(failure);

        User user = optional.get();
        if (user.getPassword().equals(password))
            return Result.ofSuccess(success, securityManager.signIn(user.getId()));
        else
            return Result.ofFailure(failure);
    }

    public void signOut(SecurityToken token) {
        securityManager.signOut(token);
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // User related operations                                                                                        //
    // -------------------------------------------------------------------------------------------------------------- //

    public Result<List<AccountDto>> getUserAccounts(SecurityToken token) {
        try {
            if (securityManager.isTokenInvalid(token))
                return Result.ofFailure(Status.BAD_TOKEN);
            Long userId = securityManager.getUserId(token);

            List<Account> accounts = AccountService.getUserAccounts(userId);
            List<AccountDto> accountDtos = accounts.stream().map(this::convertAccount).collect(Collectors.toList());
            return Result.ofSuccess(Status.GET_USER_ACCOUNTS_SUCCESS, accountDtos);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    public Result<List<OperationDto>> getUserOperations(SecurityToken token) {
        try {
            if (securityManager.isTokenInvalid(token))
                return Result.ofFailure(Status.BAD_TOKEN);
            Long userId = securityManager.getUserId(token);

            List<Account> accounts = AccountService.getUserAccounts(userId);
            Stream<OperationDto> stream = Stream.empty();
            for (Account account : accounts) {
                UUID accountId = account.getId();
                List<Operation> operations = OperationService.findAllByAccountId(accountId);
                stream = Stream.concat(
                        stream,
                        operations.stream().map(op -> convertOperation(op, accountId))
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

    // -------------------------------------------------------------------------------------------------------------- //
    // Account related operations                                                                                     //
    // -------------------------------------------------------------------------------------------------------------- //

    public Status createAccount(SecurityToken token, Currency currency) {
        try {
            if (securityManager.isTokenInvalid(token))
                return Status.BAD_TOKEN;
            Long userId = securityManager.getUserId(token);

            AccountService.add(new Account(userId, currency));
            return Status.CREATE_ACCOUNT_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    public Status setPrimaryAccount(SecurityToken token, UUID accountId) {
        try {
            if (securityManager.isTokenInvalid(token))
                return Status.BAD_TOKEN;
            Long userId = securityManager.getUserId(token);

            UserService.setPrimaryAccountId(userId, accountId);
            return Status.SET_PRIMARY_ACCOUNT_SUCCESS;
        } catch (DataAccessException e) {
            return Status.FAILURE_INTERNAL_ERROR;
        }
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operations with funds                                                                                          //
    // -------------------------------------------------------------------------------------------------------------- //

    public Status depositFunds(SecurityToken token, UUID accountId, Currency currency, BigDecimal amount) {
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

    public Status transferFunds(SecurityToken token, UUID senderAccountId, String receiverPhoneNumber,
                                BigDecimal amount, Currency currency) {
        try {
            Result<Account> result = getAccountEntity(token, senderAccountId);
            if (result.failure())
                return result.getStatus();
            Account senderAccount = result.getData();

            Optional<User> userOptional = UserService.getByPhoneNumber(receiverPhoneNumber);
            if (!userOptional.isPresent())
                return Status.TRANSFER_FUNDS_FAILURE_INVALID_PHONE_NUMBER;
            User receiver = userOptional.get();

            int count = AccountService.countUserAccounts(receiver.getId());
            if (count == 0)
                return Status.TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_ACCOUNTS;

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
    // Operation logging                                                                                              //
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

    // -------------------------------------------------------------------------------------------------------------- //
    // Helper methods                                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    private Result<Account> getAccountEntity(SecurityToken token, UUID accountId) {
        try {
            if (securityManager.isTokenInvalid(token))
                return Result.ofFailure(Status.BAD_TOKEN);
            Long userId = securityManager.getUserId(token);

            Optional<Account> optional = AccountService.get(accountId);
            if (!optional.isPresent())
                return Result.ofFailure(Status.FAILURE_INVALID_ACCOUNT_ID);

            Account account = optional.get();
            if (!account.getUserId().equals(userId))
                return Result.ofFailure(Status.FAILURE_UNAUTHORIZED_ACCESS);

            return Result.ofSuccess(Status.SUCCESS, account);
        } catch (DataAccessException e) {
            return Result.ofFailure(Status.FAILURE_INTERNAL_ERROR);
        }
    }

    private AccountDto convertAccount(Account account) {
        return new AccountDto(
                account.getId(),
                account.getBalance(),
                account.getCurrency()
        );
    }

    private OperationDto convertOperation(Operation operation, UUID accountId) {
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
