package com.github.tehArchitecht.jdbcbankingapp.presentation.util;

import com.github.tehArchitecht.jdbcbankingapp.logic.Result;
import com.github.tehArchitecht.jdbcbankingapp.logic.Status;

/**
 * Provides textual representation for status values returned by the BankService
 * class.
 */
public class StatusMapper {
    public static String statusToString(Status status) {
        switch (status) {
            // status values shared by methods
            case FAILURE_BAD_TOKEN:
                return "Ошибка. Недействительный токен. Попробуйте заново войти в систему.";
            case FAILURE_INTERNAL_ERROR:
                return "Ошибка. Произошёл сбой в системе.";
            case FAILURE_INVALID_ACCOUNT_ID:
                return "Ошибка. Такого аккаунта не существует.";
            case FAILURE_UNAUTHORIZED_ACCESS:
                return "Ошибка. Несанкционированный доступ.";
            case FAILURE_VALIDATION_ERROR:
                return "Ошибка валидации. В запросе присутствуют пустые поля либо неположительные значения.";
            // signUp
            case SIGN_UP_SUCCESS:
                return "Регистрация прошла успешно.";
            case SING_UP_FAILURE_NAME_OR_PHONE_NUMBER_TAKEN:
                return "Ошибка. Пользователь с таким логином или телефоном уже зарегистрирован в системе.";
            // signInWithName
            case SIGN_IN_WITH_NAME_SUCCESS:
                return "Вход в систему прошёл успешно.";
            case SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA:
                return "Ошибка. Неверный логин и/или пароль.";
            // signWithPhoneNumber
            case SIGN_IN_WITH_PHONE_NUMBER_SUCCESS:
                return "Вход в систему прошёл успешно.";
            case SIGN_IN_WITH_PHONE_NUMBER_FAILURE_WRONG_DATA:
                return "Ошибка. Неверный номер телефона и/или пароль.";
            // getUserOperations
            case GET_USER_OPERATIONS_SUCCESS:
                return null;
            // getUserAccounts
            case GET_USER_ACCOUNTS_SUCCESS:
                return null;
            // createAccount
            case CREATE_ACCOUNT_SUCCESS:
                return "Счёт создан.";
            // setPrimaryAccount
            case SET_PRIMARY_ACCOUNT_SUCCESS:
                return "Основной счёт установлен.";
            // depositFunds
            case DEPOSIT_FUNDS_SUCCESS:
                return "Пополнение счета прошло успешно.";
            // transferFunds
            case TRANSFER_FUNDS_SUCCESS:
                return "Перевод прошел успешно.";
            case TRANSFER_FUNDS_FAILURE_INVALID_PHONE_NUMBER:
                return "Ошибка. Пользователя с таким номером телефона не существует.";
            case TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_PRIMARY_ACCOUNT:
                return "Ошибка. У получателя нет основного счёта.";
            case TRANSFER_FUNDS_FAILURE_SAME_ACCOUNT:
                return "Ошибка. Попытка перевода средств на счёт списания.";
            case TRANSFER_FUNDS_FAILURE_INSUFFICIENT_FUNDS:
                return "Ошибка. На счету недостаточно средств.";
            // default
            default: throw new IllegalArgumentException(status.toString());
        }
    }

    public static String statusToString(Result<?> result) {
        return statusToString(result.getStatus());
    }
}
