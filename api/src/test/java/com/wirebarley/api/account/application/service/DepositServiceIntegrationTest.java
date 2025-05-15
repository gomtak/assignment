package com.wirebarley.api.account.application.service;

import static org.junit.jupiter.api.Assertions.*;


import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.model.Account;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
@Transactional
class DepositServiceIntegrationTest {

    @Autowired
    private DepositService depositService;

    @Autowired
    private AccountPort accountPort;

    @Autowired
    private TransactionPort transactionPort;

    @Test
    void 실제_입금이_정상적으로_진행된다() {
        // given
        BigDecimal initialBalance = new BigDecimal("300000");
        BigDecimal depositAmount = new BigDecimal("50000");

        Account account = new Account(null, "ACC-123456", initialBalance);
        UUID accountId = accountPort.save(account);

        // when
        depositService.deposit(accountId, depositAmount);

        // then
        Account updated = accountPort.findById(accountId).orElseThrow();
        assertEquals(0, updated.getBalance().compareTo(new BigDecimal("350000")));
    }
}
