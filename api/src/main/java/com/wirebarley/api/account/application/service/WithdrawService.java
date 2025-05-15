package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.in.WithdrawUseCase;
import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.policy.domain.service.DailyWithdrawLimitPolicy;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;
import com.wirebarley.cache.RedisLock;
import com.wirebarley.cache.RedisLockException;
import com.wirebarley.cache.RedisLockPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class WithdrawService implements WithdrawUseCase {

    private final AccountPort accountPort;
    private final TransactionPort transactionPort;
    private final DailyWithdrawLimitPolicy withdrawLimitPolicy;

    public WithdrawService(AccountPort accountPort, TransactionPort transactionPort, DailyWithdrawLimitPolicy withdrawLimitPolicy) {
        this.accountPort = accountPort;
        this.transactionPort = transactionPort;
        this.withdrawLimitPolicy = withdrawLimitPolicy;
    }

    @Override
    @Transactional
    @RedisLock(prefix = "lock:account:", key = "#accountId")
    public void withdraw(UUID accountId, BigDecimal amount) {
        var account = accountPort.findById(accountId).orElseThrow(() -> new EntityNotFoundException("계좌가 존재하지 않습니다."));

        // 오늘의 출금 총액 조회
        var todayWithdrawn = transactionPort.sumWithdrawnToday(accountId, LocalDate.now());

        // 한도 검증
        withdrawLimitPolicy.validate(todayWithdrawn, amount);

        // 출금 수행
        account.withdraw(amount, BigDecimal.ZERO);

        // 저장
        accountPort.save(account);
        transactionPort.saveWithdrawal(accountId, amount);
    }
}
