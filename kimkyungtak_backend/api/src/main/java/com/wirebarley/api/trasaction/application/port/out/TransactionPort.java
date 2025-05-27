package com.wirebarley.api.trasaction.application.port.out;

import com.wirebarley.api.trasaction.adapters.dto.TransactionResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface TransactionPort {
    BigDecimal sumWithdrawnToday(UUID accountId, LocalDate now);

    void saveWithdrawal(UUID accountId, BigDecimal amount);

    BigDecimal sumTransferredToday(UUID fromAccountId, LocalDate now);

    void saveTransfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, BigDecimal fee);

    List<TransactionResponse> findAllByAccountId(UUID accountId);

    void saveDeposit(UUID accountId, BigDecimal amount);
}
