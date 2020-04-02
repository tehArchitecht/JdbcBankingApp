package com.github.tehArchitecht.jdbcbankingapp.presentation;

import com.github.tehArchitecht.jdbcbankingapp.data.model.Currency;
import com.github.tehArchitecht.jdbcbankingapp.logic.Result;
import com.github.tehArchitecht.jdbcbankingapp.logic.Status;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.request.*;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.response.AccountDto;
import com.github.tehArchitecht.jdbcbankingapp.logic.dto.response.OperationDto;
import com.github.tehArchitecht.jdbcbankingapp.security.SecurityToken;
import com.github.tehArchitecht.jdbcbankingapp.logic.service.BankService;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * A TUI for the BankService class.
 */
public class BankTui {
    private final static Logger logger = Logger.getLogger(BankTui.class);

    private final InputReader in;
    private final PrintStream out;

    private final BankService bankService;

    private SecurityToken token = null;
    private boolean running;

    public BankTui(InputStream in, PrintStream out) {
        this.in = new InputReader(in);
        this.out = out;
        this.bankService = new BankService();
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Primary screens (methods)                                                                                      //
    // -------------------------------------------------------------------------------------------------------------- //

    public void run() {
        out.println("БАНКОВСКОЕ ПРИЛОЖЕНИЕ");

        running = true;
        while (running) {
            out.println();
            if (token == null)
                unauthorizedScreen();
            else
                authorizedScreen();
        }
    }

    private void unauthorizedScreen() {
        out.println("Доступные действия:");
        out.println("1. Зарегистрироваться в системе");
        out.println("2. Войти в систему");
        out.println("3. Выйти из программы");
        out.print("Введите номер (1, 2, 3): ");

        int optionNumber = inputOptionNumber(3);

        out.println();
        switch (optionNumber) {
            case 1: signUp(); break;
            case 2: signIn(); break;
            case 3: running = false; break;
        }
    }

    private void authorizedScreen() {
        out.println("Доступные действия:");
        out.println("1. Создать счёт");
        out.println("2. Изменить основной счёт");
        out.println("3. Внести средства");
        out.println("4. Перевести средства");
        out.println("5. Просмотреть историю операций");
        out.println("6. Просмотреть привязанные счета");
        out.println("7. Выйти из системы (аккаунта)");
        out.println("8. Выйти из программы");
        out.print("Введите номер (1, 2, 3, 4, 5, 6, 7, 8): ");

        int optionNumber = inputOptionNumber(8);

        out.println();
        switch (optionNumber) {
            case 1: createAccount(); break;
            case 2: setPrimaryAccount(); break;
            case 3: depositFunds(); break;
            case 4: transferFunds(); break;
            case 5: displayOperationHistory(); break;
            case 6: displayUserAccounts(); break;
            case 7: signOut(); break;
            case 8: running = false; break;
        }
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Screens for unauthorized users                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    private void signUp() {
        out.println("-- Регистрация --");
        SignUpRequest request = new SignUpRequest();

        out.print("Введите логин: ");
        request.setUserName(inputUserName());

        out.print("Введите пароль: ");
        request.setPassword(inputPassword());

        out.print("Введите телефон: ");
        request.setPhoneNumber(inputPhoneNumber());

        out.print("Введите адрес: ");
        request.setAddress(inputAddress());

        Status status = bankService.signUp(request);
        displayStatus(status);
    }

    private void signIn() {
        out.println("Доступные варианты:");
        out.println("1. Войти по логину");
        out.println("2. Войти по номеру телефона");
        out.print("Введите номер (1, 2): ");

        int optionNumber = inputOptionNumber(2);

        out.println();
        switch (optionNumber) {
            case 1: signInWithName(); break;
            case 2: signInWithPhoneNumber(); break;
        }
    }

    private void signInWithName() {
        out.println("-- Вход по логину --");
        SignInWithNameRequest request = new SignInWithNameRequest();

        out.print("Введите логин: ");
        request.setUserName(inputUserName());

        out.print("Введите пароль: ");
        request.setPassword(inputPassword());

        Result<SecurityToken> result = bankService.signInWithName(request);
        token = result.getData();

        displayStatus(result);
    }

    private void signInWithPhoneNumber() {
        out.println("-- Вход по номеру телефона --");
        SignInWithPhoneNumberRequest request = new SignInWithPhoneNumberRequest();

        out.print("Введите номер телефона: ");
        request.setPhoneNumber(inputPhoneNumber());

        out.print("Введите пароль: ");
        request.setPassword(inputPassword());

        Result<SecurityToken> result = bankService.signWithPhoneNumber(request);
        token = result.getData();

        displayStatus(result);
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Screens for authorized users                                                                                   //
    // -------------------------------------------------------------------------------------------------------------- //

    private void createAccount() {
        out.println("-- Создание счёта --");
        CreateAccountRequest request = new CreateAccountRequest();

        out.print("Введите валюту (EUR, RUB, USD): ");
        request.setCurrency(inputCurrency());

        Status status = bankService.createAccount(token, request);
        displayStatus(status);
    }

    private void setPrimaryAccount() {
        out.println("-- Изменение основного счёта --");
        SetPrimaryAccountRequest request = new SetPrimaryAccountRequest();

        UUID accountId = selectAccount("Выберите счёт: ");
        if (accountId == null) return;
        request.setAccountId(accountId);

        Status status = bankService.setPrimaryAccount(token, request);
        displayStatus(status);
    }

    private void depositFunds() {
        out.println("-- Пополнение счёта --");
        DepositFundsRequest request = new DepositFundsRequest();

        UUID accountId = selectAccount("Выберите счёт: ");
        if (accountId == null) return;
        request.setAccountId(accountId);

        out.print("Введите валюту (EUR, RUB, USD): ");
        request.setCurrency(inputCurrency());

        out.print("Введите сумму: ");
        request.setAmount(inputFunds());

        Status status = bankService.depositFunds(token, request);
        out.println(StatusMapper.statusToString(status));
    }

    private void transferFunds() {
        out.println("-- Перевод средств --");
        TransferFundsRequest request = new TransferFundsRequest();

        UUID accountId = selectAccount("Выберите счёт для списания средств: ");
        if (accountId == null) return;
        request.setSenderAccountId(accountId);

        out.print("Введите номер телефона получателя: ");
        request.setReceiverPhoneNumber(inputPhoneNumber());

        out.print("Введите валюту (EUR, RUB, USD): ");
        request.setCurrency(inputCurrency());

        out.print("Введите сумму: ");
        request.setAmount(inputFunds());

        Status status = bankService.transferFunds(token, request);
        displayStatus(status);
    }

    private void displayOperationHistory() {
        out.println("-- Просмотр истории операций --");

        Result<List<OperationDto>> result = bankService.getUserOperations(token);
        if (handleFailedResult(result)) return;

        List<OperationDto> operations = result.getData();
        if (operations.isEmpty()) {
            out.println("История операций по вашему аккаунту пуста.");
        } else {
            out.println("Список операций по вашим счетам:");
            printOperations(operations);
        }
    }

    private void displayUserAccounts() {
        out.println("-- Просмотр счетов --");

        Result<List<AccountDto>> result = bankService.getUserAccounts(token);
        if (handleFailedResult(result)) return;

        List<AccountDto> accounts = result.getData();
        if (accounts.isEmpty()) {
            out.println("У вас нет ни одного счёта.");
        } else {
            out.println("Список ваших счетов:");
            printAccounts(accounts);
        }
    }

    private void signOut() {
        out.println("-- Выход из аккаунта --");

        bankService.signOut(token);
        token = null;
        out.println("Вы успешно вышли из системы.");
    }

    // -------------------------------------------------------------------------------------------------------------- //
    // Helper methods                                                                                                 //
    // -------------------------------------------------------------------------------------------------------------- //

    // Input methods ------------------------------------------------------------------------------------------------ //

    private String inputUserName() { return in.readLine(); }
    private String inputPhoneNumber() { return in.readLine(); }
    private String inputPassword() { return in.readLine(); }
    private String inputAddress() { return in.readLine(); }

    private void waitForEnter() { in.readLine(); }

    private UUID selectAccount(String message) {
        Result<List<AccountDto>> result = bankService.getUserAccounts(token);
        if (handleFailedResult(result)) return null;

        List<AccountDto> accounts = result.getData();
        if (accounts.isEmpty()) {
            out.println("Ошибка. У вас нет ни одного счета.");
            return null;
        }

        return selectAccount(accounts, message);
    }

    private UUID selectAccount(List<AccountDto> accounts, String message) {
        int count = accounts.size();
        if (count == 1) {
            out.println("Вы имеете единственный счёт в системе: ");
            printAccounts(accounts);

            out.print("Нажмите Enter, чтобы продолжить...");
            waitForEnter();
            return accounts.get(0).getId();
        } else {
            out.println(message);
            printAccounts(accounts);

            out.print("Введите номер (число от 1 до " + count + "): ");
            int optionNumber = inputOptionNumber(count);
            return accounts.get(optionNumber-1).getId();
        }
    }

    private int inputOptionNumber(int numOptions) {
        while (true) {
            try {
                int optionNumber = in.readInt();
                if (optionNumber >= 1 && optionNumber <= numOptions)
                    return optionNumber;
            } catch (NumberFormatException e) {
                logger.warn(e);
            }
            out.print("Ошибка. Введите целое число от 1 до " + numOptions + ": ");
        }
    }

    private Currency inputCurrency() {
        while (true) {
            try {
                return in.readCurrency();
            } catch (IllegalArgumentException e) {
                logger.warn(e);
            }
            out.print("Ошибка. Введите одно из значений EUR, RUB, USD: ");
        }
    }

    private BigDecimal inputFunds() {
        while (true) {
            try {
                return in.readBigDecimal();
            } catch (NumberFormatException e) {
                logger.warn(e);
            }
            out.print("Ошибка. Введите вещественное число: ");
        }
    }

    // Output methods ----------------------------------------------------------------------------------------------- //

    private void printAccounts(List<AccountDto> accounts) {
        out.println(DataPresenter.accountListToString(accounts));
    }

    private void printOperations(List<OperationDto> operations) {
        out.println(DataPresenter.operationListToString(operations));
    }

    // Methods that simplify common code ---------------------------------------------------------------------------- //

    private void displayStatus(Status status) {
        out.println(StatusMapper.statusToString(status));
    }

    private void displayStatus(Result<?> result) {
        out.println(StatusMapper.statusToString(result));
    }

    private boolean handleFailedResult(Result<?> result) {
        if (result.failure()) {
            displayStatus(result);
            return true;
        } else {
            return false;
        }
    }
}
