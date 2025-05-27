package com.wirebarley.api.account.domain.exception;

public class ExistsAccountNumberException extends RuntimeException {
    public ExistsAccountNumberException(String message) {
        super(message);
    }
}
