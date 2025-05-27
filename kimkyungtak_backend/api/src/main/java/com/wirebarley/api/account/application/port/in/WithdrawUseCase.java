package com.wirebarley.api.account.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface WithdrawUseCase {
    void withdraw(UUID accountId, BigDecimal amount);
}
