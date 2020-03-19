package com.github.tehArchitecht.logic;

import java.util.Optional;

public class Result<T> {
    private boolean success;
    private Status status;
    private T data;

    public static <T> Result<T> ofSuccess(Status status, T data) {
        return new Result<>(true, status, data);
    }

    public static <T> Result<T> ofFailure(Status status) {
        return new Result<>(false, status, null);
    }

    public static <T> Result<T> fromOptional(Optional<T> optional, Status success, Status failure) {
        return optional.isPresent() ? ofSuccess(success, optional.get()) : ofFailure(failure);
    }

    private Result(boolean success, Status status, T data) {
        this.success = success;
        this.status = status;
        this.data = data;
    }

    public boolean failure() {
        return !this.success;
    }

    public T getData() {
        return this.data;
    }

    public Status getStatus() {
        return this.status;
    }
}
