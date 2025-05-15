package com.wirebarley.api.account.application.port.in;

import com.wirebarley.api.trasaction.adapters.dto.TransactionResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface TransferUseCase {
    void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount);
    List<TransactionResponse> getTransactions(UUID accountId);
}
