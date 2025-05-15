package com.wirebarley.api.account.domain.exception;

import java.math.BigDecimal;

public class InvalidWithdrawAmountException extends RuntimeException {
    public InvalidWithdrawAmountException(BigDecimal amount) {
        super("출금액은 0보다 커야 합니다: " + amount);
    }
}
