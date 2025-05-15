package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.model.Account;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DepositServiceTest {

    @Mock
    private AccountPort accountPort;

    @Mock
    private TransactionPort transactionPort;

    @InjectMocks
    private DepositService depositService;

    @Test
    void 정상적으로_입금된다() {
        // given
        var accountId = UUID.randomUUID();
        var initial = new BigDecimal("500000");
        var amount = new BigDecimal("100000");

        var account = new Account(accountId, "1234567890", initial);
        given(accountPort.findById(accountId)).willReturn(Optional.of(account));

        // when
        depositService.deposit(accountId, amount);

        // then
        verify(accountPort).save(account);
        verify(transactionPort).saveDeposit(accountId, amount);
        assertEquals(0, account.getBalance().compareTo(new BigDecimal("600000")));
    }

    @Test
    void 존재하지_않는_계좌는_예외() {
        UUID accountId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("100000");

        given(accountPort.findById(accountId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                depositService.deposit(accountId, amount)
        );
    }
}

