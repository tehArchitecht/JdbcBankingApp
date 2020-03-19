package com.github.tehArchitecht.logic.service;

import com.github.tehArchitecht.data.model.Account;
import com.github.tehArchitecht.data.model.Currency;
import com.github.tehArchitecht.data.model.Operation;
import com.github.tehArchitecht.data.model.User;
import com.github.tehArchitecht.logic.CurrencyConverter;
import com.github.tehArchitecht.logic.Result;
import com.github.tehArchitecht.logic.Status;
import com.github.tehArchitecht.logic.dto.*;
import com.github.tehArchitecht.logic.security.SecurityManager;
import com.github.tehArchitecht.logic.security.SecurityToken;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BankService {
    private SecurityManager securityManager;

    public BankService() {
        this.securityManager = new SecurityManager();
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Registration and authorisation operations                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

    public Status signUp (String name, String password, String address, String phoneNumber) {
        boolean success = UserService.add(new User(
                null,
                name,
                password,
                address,
                phoneNumber
        ));
        return success ? Status.SIGN_UP_SUCCESS : Status.SIGN_UP_FAILURE_INTERNAL_ERROR;
    }

    public Result<Boolean> isNameInUse(String name) {
        return Result.fromOptional(
                UserService.isNameInUse(name),
                Status.IS_NAME_IN_USE_SUCCESS,
                Status.IS_NAME_IN_USE_FAILURE_INTERNAL_ERROR
        );
    }

    public Result<SecurityToken> signInWithName(String userName, String password) {
        Optional<Boolean> optional = UserService.isNameInUse(userName);
        if (!optional.isPresent()) return Result.ofFailure(Status.SIGN_IN_FAILURE_INTERNAL_ERROR);
        if (!optional.get()) return Result.ofFailure(Status.SIGN_IN_FAILURE_WRONG_DATA);

        return signIn(UserService.getByName(userName), password);
    }

    public Result<SecurityToken> signWithPhoneNumber(String phoneNumber, String password) {
        Optional<Boolean> optional = UserService.isPhoneNumberInUse(phoneNumber);
        if (!optional.isPresent()) return Result.ofFailure(Status.SIGN_IN_FAILURE_INTERNAL_ERROR);
        if (!optional.get()) return Result.ofFailure(Status.SIGN_IN_FAILURE_WRONG_DATA);

        return signIn(UserService.getByPhoneNumber(phoneNumber), password);
    }

    private Result<SecurityToken> signIn(Optional<User> optional, String password) {
        if (!optional.isPresent())
            return Result.ofFailure(Status.SIGN_IN_FAILURE_INTERNAL_ERROR);

        User user = optional.get();
        if (user.getPassword().equals(password))
            return Result.ofSuccess(Status.SIGN_IN_SUCCESS, securityManager.signIn(user.getId()));
        else
            return Result.ofFailure(Status.SIGN_IN_FAILURE_WRONG_DATA);
    }

    public void signOut(SecurityToken token) {
        securityManager.signOut(token);
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // User related operations                                                                                        //
    // -------------------------------------------------------------------------------------------------------------- //

    public Result<List<AccountDto>> getUserAccounts(SecurityToken token) {
        if (securityManager.isTokenInvalid(token))
            return Result.ofFailure(Status.BAD_TOKEN);
        Long userId = securityManager.getUserId(token);

        Optional<List<Account>> optional = AccountService.getUserAccounts(userId);
        if (!optional.isPresent())
            return Result.ofFailure(Status.GET_USER_ACCOUNTS_FAILURE_INTERNAL_ERROR);

        List<AccountDto> accounts = optional.get().stream().map(this::convertAccount).collect(Collectors.toList());
        return Result.ofSuccess(Status.GET_USER_ACCOUNTS_SUCCESS, accounts);
    }

    public Result<List<OperationDto>> getUserOperations(SecurityToken token) {
        if (securityManager.isTokenInvalid(token))
            return Result.ofFailure(Status.BAD_TOKEN);
        Long userId = securityManager.getUserId(token);

        Optional<List<Account>> optional = AccountService.getUserAccounts(userId);
        if (!optional.isPresent())
            return Result.ofFailure(Status.GET_USER_OPERATIONS_FAILURE_INTERNAL_ERROR);

        List<Account> accounts = optional.get();
        Stream<OperationDto> operations = Stream.empty();
        for (Account account : accounts) {
            UUID accountId = account.getId();

            Optional<List<Operation>> operationsOptional = OperationService.findAllByAccountId(accountId);
            if (!operationsOptional.isPresent())
                return Result.ofFailure(Status.GET_USER_OPERATIONS_FAILURE_INTERNAL_ERROR);

            operations = Stream.concat(
                    operations,
                    operationsOptional.get().stream().map(op -> convertOperation(op, accountId))
            );
        }

        return Result.ofSuccess(
                Status.GET_USER_OPERATIONS_SUCCESS,
                operations.sorted(Comparator.comparing(OperationDto::getDate)).collect(Collectors.toList())
        );
    }

    public Result<Boolean> userHasAccounts(SecurityToken token) {
        if (securityManager.isTokenInvalid(token))
            return Result.ofFailure(Status.BAD_TOKEN);
        Long userId = securityManager.getUserId(token);

        Optional<Integer> optional = AccountService.countUserAccounts(userId);
        if (!optional.isPresent()) return Result.ofFailure(Status.USER_HAS_ACCOUNTS_FAILURE_INTERNAL_ERROR);

        return Result.ofSuccess(Status.USER_HAS_ACCOUNTS_SUCCESS, optional.get() > 0);
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Account related operations                                                                                     //
    // -------------------------------------------------------------------------------------------------------------- //

    public Status createAccount(SecurityToken token, Currency currency) {
        if (securityManager.isTokenInvalid(token))
            return Status.BAD_TOKEN;
        Long userId = securityManager.getUserId(token);

        boolean saved = AccountService.add(new Account(userId, currency));
        return saved ? Status.CREATE_ACCOUNT_SUCCESS : Status.CREATE_ACCOUNT_FAILURE_INTERNAL_ERROR;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operations with funds                                                                                          //
    // -------------------------------------------------------------------------------------------------------------- //

    public Status depositFunds(SecurityToken token, UUID accountId, Currency currency, BigDecimal amount) {
        Result<Account> result = getAccountEntity(token, accountId);
        if (result.failure()) {
            switch (result.getStatus()) {
                case BAD_TOKEN: return Status.BAD_TOKEN;
                case INVALID_ACCOUNT_ID: return Status.DEPOSIT_FUNDS_FAILURE_INVALID_ACCOUNT_ID;
                case INTERNAL_ERROR: return Status.DEPOSIT_FUNDS_FAILURE_INTERNAL_ERROR;
                case UNAUTHORIZED_ACCESS: return Status.DEPOSIT_FUNDS_FAILURE_UNAUTHORIZED_ACCESS;
            }
        }

        Account account = result.getData();
        BigDecimal converted = CurrencyConverter.convert(amount, currency, account.getCurrency());
        BigDecimal newBalance = account.getBalance().add(converted);
        boolean updated = AccountService.setBalance(account.getId(), newBalance);
        return updated ? Status.DEPOSIT_FUNDS_SUCCESS : Status.DEPOSIT_FUNDS_FAILURE_INTERNAL_ERROR;
    }

    public Status transferFunds(SecurityToken token, UUID senderAccountId, String receiverPhoneNumber,
                                BigDecimal amount, Currency currency) {
        Result<Account> result = getAccountEntity(token, senderAccountId);
        if (result.failure()) {
            switch (result.getStatus()) {
                case BAD_TOKEN: return Status.BAD_TOKEN;
                case INVALID_ACCOUNT_ID: return Status.TRANSFER_FUNDS_FAILURE_INVALID_ACCOUNT_ID;
                case INTERNAL_ERROR: return Status.TRANSFER_FUNDS_FAILURE_INTERNAL_ERROR;
                case UNAUTHORIZED_ACCESS: return Status.TRANSFER_FUNDS_FAILURE_UNAUTHORIZED_ACCESS;
            }
        }

        Account senderAccount = result.getData();

        Optional<User> userOptional = UserService.getByPhoneNumber(receiverPhoneNumber);
        if (!userOptional.isPresent())
            return Status.TRANSFER_FUNDS_FAILURE_INTERNAL_ERROR;
        User receiver = userOptional.get();

        Optional<Integer> optionalCount = AccountService.countUserAccounts(receiver.getId());
        if (!optionalCount.isPresent())
            return Status.TRANSFER_FUNDS_FAILURE_INTERNAL_ERROR;

        if (optionalCount.get() == 0)
            return Status.TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_ACCOUNTS;

        Optional<Account> accountOptional = AccountService.getUserPrimaryAccount(receiver.getId());
        if (!accountOptional.isPresent())
            return Status.TRANSFER_FUNDS_FAILURE_INTERNAL_ERROR;
        Account receiverAccount = accountOptional.get();

        BigDecimal senderInitialBalance = senderAccount.getBalance();
        BigDecimal receiverInitialBalance = receiverAccount.getBalance();
        Currency senderCurrency = senderAccount.getCurrency();
        Currency transferCurrency = currency;
        Currency receiverCurrency = receiverAccount.getCurrency();

        BigDecimal senderAmount = CurrencyConverter.convert(amount, transferCurrency, senderCurrency);
        BigDecimal receiverAmount = CurrencyConverter.convert(amount, transferCurrency, receiverCurrency);

        if (senderAccount.getBalance().compareTo(senderAmount) < 0)
            return Status.TRANSFER_FUNDS_FAILURE_INSUFFICIENT_FUNDS;

        boolean set = true;
        set &= AccountService.setBalance(senderAccount.getId(), senderInitialBalance.subtract(senderAmount));
        set &= AccountService.setBalance(receiverAccount.getId(), receiverInitialBalance.add(receiverAmount));
        if (!set)
            return Status.TRANSFER_FUNDS_FAILURE_INTERNAL_ERROR;

        if (!logTransfer(senderAccount, receiverAccount, currency, amount, senderInitialBalance, receiverInitialBalance))
            return Status.TRANSFER_FUNDS_FAILURE_INTERNAL_ERROR;

        return Status.TRANSFER_FUNDS_SUCCESS;
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Operation logging                                                                                              //
    // -------------------------------------------------------------------------------------------------------------- //

    private static boolean logTransfer(Account sender, Account receiver, Currency currency, BigDecimal amount,
                                       BigDecimal senderInitialBalance, BigDecimal receiverInitialBalance) {
        return OperationService.add(new Operation(
                null,
                new Timestamp(System.currentTimeMillis()),
                currency,
                sender.getId(),
                receiver.getId(),
                amount,
                senderInitialBalance,
                senderInitialBalance.subtract(amount),
                receiverInitialBalance,
                receiverInitialBalance.add(amount)
        ));
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Helper methods                                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    private Result<Account> getAccountEntity(SecurityToken token, UUID accountId) {
        if (securityManager.isTokenInvalid(token))
            return Result.ofFailure(Status.BAD_TOKEN);
        Long userId = securityManager.getUserId(token);

        Optional<Boolean> existsOptional = AccountService.exists(accountId);
        if (!existsOptional.isPresent())
            return Result.ofFailure(Status.INTERNAL_ERROR);

        boolean accountExists = existsOptional.get();
        if (!accountExists)
            return Result.ofFailure(Status.INVALID_ACCOUNT_ID);

        Optional<Account> optional = AccountService.get(accountId);
        if (!optional.isPresent())
            return Result.ofFailure(Status.INTERNAL_ERROR);

        Account account = optional.get();
        if (account.getUserId() != userId)
            return Result.ofFailure(Status.UNAUTHORIZED_ACCESS);

        return Result.ofSuccess(Status.SUCCESS, account);
    }

    private AccountDto convertAccount(Account account) {
        return new AccountDto(
                account.getId(),
                account.getBalance(),
                account.getCurrency()
        );
    }

    private OperationDto convertOperation(Operation operation, UUID accountId) {
        if (accountId == operation.getSenderAccountId()) {
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
