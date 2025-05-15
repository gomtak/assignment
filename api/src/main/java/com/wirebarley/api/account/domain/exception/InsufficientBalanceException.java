package com.wirebarley.api.account.domain.exception;

import java.math.BigDecimal;
import java.util.UUID;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(UUID accountId, BigDecimal balance, BigDecimal required) {
        super(String.format("잔액이 부족합니다. accountId=%s, 잔액=%,.0f원, 요청=%,.0f원",
                accountId, balance, required));
    }
}
