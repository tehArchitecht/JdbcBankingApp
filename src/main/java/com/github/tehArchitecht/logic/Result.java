package com.github.tehArchitecht.logic;

/**
 * Represents results (status and possibly a value) of BankService operations.
 * Result objects are created using factory methods: ofSuccess, ofFailure, fromOptional.
 */
public class Result<T> {
    private final Status status;
    private final T data;

    public static <T> Result<T> ofSuccess(Status status, T data) {
        return new Result<>(status, data);
    }

    public static <T> Result<T> ofFailure(Status status) {
        return new Result<>(status, null);
    }

    private Result(Status status, T data) {
        this.status = status;
        this.data = data;
    }

    public boolean failure() {
        return this.data == null;
    }

    public T getData() {
        return this.data;
    }

    public Status getStatus() {
        return this.status;
    }
}
