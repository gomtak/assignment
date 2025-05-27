package com.wirebarley.api.trasaction.adapters.out;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.wirebarley.api.common.annotation.PersistenceAdapter;
import com.wirebarley.api.trasaction.adapters.dto.TransactionResponse;
import com.wirebarley.api.trasaction.adapters.out.entity.TransactionEntity;
import com.wirebarley.api.trasaction.adapters.out.entity.TransactionType;
import com.wirebarley.api.trasaction.adapters.out.repository.TransactionRepository;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.wirebarley.api.trasaction.adapters.out.entity.QTransactionEntity.transactionEntity;

@PersistenceAdapter
public class TransactionAdapter implements TransactionPort {
    private final TransactionRepository transactionRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public TransactionAdapter(TransactionRepository transactionRepository, JPAQueryFactory jpaQueryFactory) {
        this.transactionRepository = transactionRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public BigDecimal sumWithdrawnToday(UUID accountId, LocalDate now) {
        var result = jpaQueryFactory
                .select(transactionEntity.amount.sum())
                .from(transactionEntity)
                .where(
                        byAccount(accountId),
                        byTransactionType(TransactionType.WITHDRAW),
                        onDate(now)
                )
                .fetchOne();
        return result != null ? result : BigDecimal.ZERO;
    }

    private static BooleanExpression onDate(LocalDate date) {
        var start = date.atStartOfDay();
        var end = date.plusDays(1).atStartOfDay();

        return transactionEntity.createdDatetime.between(start, end);
    }

    private static BooleanExpression byTransactionType(TransactionType transactionType) {
        return transactionEntity.type.eq(transactionType);
    }

    private static BooleanExpression byAccount(UUID accountId) {
        return transactionEntity.accountId.eq(accountId);
    }

    @Override
    public void saveWithdrawal(UUID accountId, BigDecimal amount) {
        transactionRepository.save(new TransactionEntity(accountId, TransactionType.WITHDRAW, amount, null, BigDecimal.ZERO));
    }

    @Override
    public BigDecimal sumTransferredToday(UUID accountId, LocalDate date) {
        var result = jpaQueryFactory
                .select(transactionEntity.amount.sum())
                .from(transactionEntity)
                .where(
                        byAccount(accountId),
                        byTransactionType(TransactionType.TRANSFER),
                        onDate(date)
                )
                .fetchOne();

        return result != null ? result : BigDecimal.ZERO;
    }

    @Override
    public void saveTransfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount, BigDecimal fee) {
        // 출금자 입장에서 기록
        transactionRepository.save(new TransactionEntity(fromAccountId, TransactionType.TRANSFER, amount, toAccountId, fee));

        // 수취자 입장에서 기록
        transactionRepository.save(new TransactionEntity(toAccountId, TransactionType.DEPOSIT, amount, fromAccountId, BigDecimal.ZERO));
    }

    @Override
    public List<TransactionResponse> findAllByAccountId(UUID accountId) {
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                TransactionResponse.class,
                                transactionEntity.accountId,
                                transactionEntity.type,
                                transactionEntity.amount,
                                transactionEntity.fee,
                                transactionEntity.counterpartyAccountId,
                                transactionEntity.createdDatetime
                        )
                )
                .from(transactionEntity)
                .where(transactionEntity.accountId.eq(accountId))
                .orderBy(transactionEntity.createdDatetime.desc())
                .fetch();
    }

    @Override
    public void saveDeposit(UUID accountId, BigDecimal amount) {
        transactionRepository.save(new TransactionEntity(accountId, TransactionType.DEPOSIT, amount, null, BigDecimal.ZERO));
    }
}
