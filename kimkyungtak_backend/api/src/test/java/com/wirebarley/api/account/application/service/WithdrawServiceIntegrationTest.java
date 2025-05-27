package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.exception.InsufficientBalanceException;
import com.wirebarley.api.account.domain.model.Account;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WithdrawServiceIntegrationTest {

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private AccountPort accountPort;

    @Autowired
    private TransactionPort transactionPort;

    @Test
    void 실제_출금이_정상적으로_진행된다() {
        // given
        BigDecimal initialBalance = new BigDecimal("300000");
        BigDecimal withdrawAmount = new BigDecimal("50000");
        BigDecimal expectedBalance = initialBalance.subtract(withdrawAmount);
        Account account = new Account(null, "ACC-654321", initialBalance);
        UUID accountId = accountPort.save(account);

        // when
        withdrawService.withdraw(accountId, withdrawAmount);

        // then
        Account updated = accountPort.findById(accountId).orElseThrow();
        assertEquals(0, updated.getBalance().compareTo(expectedBalance));
    }

    @Test
    void 잔액보다_큰_금액을_출금하면_예외가_발생한다() {
        // given
        BigDecimal initialBalance = new BigDecimal("10000");
        BigDecimal withdrawAmount = new BigDecimal("50000");

        Account account = new Account(null, "ACC-999999", initialBalance);
        UUID accountId = accountPort.save(account);

        // when & then
        assertThrows(
                InsufficientBalanceException.class,
                () -> withdrawService.withdraw(accountId, withdrawAmount)
        );
    }
}

