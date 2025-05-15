package com.wirebarley.api.account.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface DepositUseCase {
    void deposit(UUID accountId, BigDecimal amount);
}
