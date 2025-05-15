package com.wirebarley.api.account.application.service;

import static org.junit.jupiter.api.Assertions.*;

import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.model.Account;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;
import com.wirebarley.api.policy.domain.service.TransferLimitPolicy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
@Transactional
class TransferServiceIntegrationTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountPort accountPort;

    @Autowired
    private TransactionPort transactionPort;

    @Autowired
    private TransferLimitPolicy transferLimitPolicy;

    @Test
    void 실제_계좌간_이체가_정상적으로_진행된다() {
        // given
        Account from = new Account(null, "111-111", new BigDecimal("500000"));
        Account to = new Account(null, "222-222", new BigDecimal("300000"));

        UUID fromId = accountPort.save(from);
        UUID toId = accountPort.save(to);

        // when
        transferService.transfer(fromId, toId, new BigDecimal("100000"));

        // then
        Account updatedFrom = accountPort.findById(fromId).orElseThrow();
        Account updatedTo = accountPort.findById(toId).orElseThrow();

        assertEquals(0, updatedFrom.getBalance().compareTo(new BigDecimal("399000")));
        assertEquals(0, updatedTo.getBalance().compareTo(new BigDecimal("400000")));
    }
}
