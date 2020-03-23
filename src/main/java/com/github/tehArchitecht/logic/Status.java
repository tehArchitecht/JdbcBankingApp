package com.github.tehArchitecht.logic;

/**
 * Represents status (success/failure and reason) of BankService operations.
 */
public enum Status {
    /* Status values are grouped by method.
     * INTERNAL_ERROR postfix means that DataAccessException was thrown
     * during the method call.
     */

    // status values shared by methods
    BAD_TOKEN,
    FAILURE_INTERNAL_ERROR,
    FAILURE_INVALID_ACCOUNT_ID,
    FAILURE_UNAUTHORIZED_ACCESS,
    // isNameInUse
    IS_NAME_IN_USE_SUCCESS,
    // signUp
    SIGN_UP_SUCCESS,
    SING_UP_FAILURE_NAME_OR_PHONE_NUMBER_TAKEN,
    // signInWithName
    SIGN_IN_WITH_NAME_SUCCESS,
    SIGN_IN_WITH_NAME_FAILURE_WRONG_DATA,
    // signWithPhoneNumber
    SIGN_IN_WITH_PHONE_NUMBER_SUCCESS,
    SIGN_IN_WITH_PHONE_NUMBER_FAILURE_WRONG_DATA,
    // getUserOperations
    GET_USER_OPERATIONS_SUCCESS,
    // getUserAccounts
    GET_USER_ACCOUNTS_SUCCESS,
    // createAccount
    CREATE_ACCOUNT_SUCCESS,
    // depositFunds
    DEPOSIT_FUNDS_SUCCESS,
    // transferFunds
    TRANSFER_FUNDS_SUCCESS,
    TRANSFER_FUNDS_FAILURE_INVALID_PHONE_NUMBER,
    TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_ACCOUNTS,
    TRANSFER_FUNDS_FAILURE_RECEIVER_HAS_NO_PRIMARY_ACCOUNT, // currently not used
    TRANSFER_FUNDS_FAILURE_SAME_ACCOUNT,
    TRANSFER_FUNDS_FAILURE_INSUFFICIENT_FUNDS,
    // status values used by helper methods
    SUCCESS
}
