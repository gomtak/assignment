package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.policy.domain.service.TransferLimitPolicy;
import com.wirebarley.api.trasaction.adapters.dto.TransactionResponse;
import com.wirebarley.api.account.application.port.in.TransferUseCase;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;
import com.wirebarley.cache.RedisLock;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class TransferService implements TransferUseCase {

    private final AccountPort accountPort;
    private final TransactionPort transactionPort;
    private final TransferLimitPolicy transferLimitPolicy;

    public TransferService(AccountPort accountPort,
                           TransactionPort transactionPort,
                           TransferLimitPolicy transferLimitPolicy) {
        this.accountPort = accountPort;
        this.transactionPort = transactionPort;
        this.transferLimitPolicy = transferLimitPolicy;
    }

    @Override
    @Transactional
    @RedisLock(prefix = "lock:account:", key = "#fromAccountId")
    public void transfer(UUID fromAccountId, UUID toAccountId, BigDecimal amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new IllegalArgumentException("동일한 계좌로 이체할 수 없습니다.");
        }

        var from = accountPort.findById(fromAccountId)
                .orElseThrow(() -> new EntityNotFoundException("출금 계좌가 존재하지 않습니다."));
        var to = accountPort.findById(toAccountId)
                .orElseThrow(() -> new EntityNotFoundException("입금 계좌가 존재하지 않습니다."));

        // 이체 한도 검증
        var todayTransferred = transactionPort.sumTransferredToday(fromAccountId, LocalDate.now());
        transferLimitPolicy.validate(todayTransferred, amount);

        // 수수료 계산 (1%)
        var fee = amount.multiply(new BigDecimal("0.01"));

        // 출금자 출금 (금액 + 수수료)
        from.withdraw(amount, fee);

        // 수수료 관리자 계정 입금 TODO

        // 수취자 입금 (수수료 제외 금액)
        to.deposit(amount);

        // 저장
        accountPort.save(from);
        accountPort.save(to);

        // 트랜잭션 기록 (출금, 입금)
        transactionPort.saveTransfer(fromAccountId, toAccountId, amount, fee);
    }

    @Override
    public List<TransactionResponse> getTransactions(UUID accountId) {
        return transactionPort.findAllByAccountId(accountId);
    }
}
