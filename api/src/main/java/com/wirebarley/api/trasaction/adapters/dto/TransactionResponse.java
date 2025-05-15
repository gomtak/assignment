package com.wirebarley.api.trasaction.adapters.dto;

import com.wirebarley.api.trasaction.adapters.out.entity.TransactionType;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        TransactionType type,
        BigDecimal amount,
        BigDecimal fee,
        UUID counterpartyAccountId,
        ZonedDateTime createdAt
) {
}
