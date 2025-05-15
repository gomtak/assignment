package com.wirebarley.api.account.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountCreateUseCase {
    UUID createAccount(String accountNumber, BigDecimal initialBalance);
}
