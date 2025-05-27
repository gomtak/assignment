package com.wirebarley.api.trasaction.adapters.out.entity;

import com.wirebarley.api.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "BINARY(16)")
    private UUID accountId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // WITHDRAW, DEPOSIT, TRANSFER

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = true, columnDefinition = "BINARY(16)")
    private UUID counterpartyAccountId; // 상대방 계좌 ID

    @Column(nullable = false)
    private BigDecimal fee;

    public TransactionEntity(UUID accountId, TransactionType type, BigDecimal amount, UUID counterpartyAccountId, BigDecimal fee) {
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.counterpartyAccountId = counterpartyAccountId;
        this.fee = fee;
    }
}
