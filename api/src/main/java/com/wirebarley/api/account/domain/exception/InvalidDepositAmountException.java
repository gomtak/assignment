package com.wirebarley.api.account.domain.exception;

import java.math.BigDecimal;

public class InvalidDepositAmountException extends RuntimeException {
    public InvalidDepositAmountException(BigDecimal amount) {
        super("입금액은 0보다 커야 합니다: " + amount);
    }
}



