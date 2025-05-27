package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.exception.InsufficientBalanceException;
import com.wirebarley.api.account.domain.model.Account;
import com.wirebarley.api.policy.domain.service.TransferLimitPolicy;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountPort accountPort;

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private TransferLimitPolicy transferLimitPolicy;

    @InjectMocks
    private TransferService transferService;

    @Test
    void 정상적으로_이체된다() {
        // given
        var fromId = UUID.randomUUID();
        var toId = UUID.randomUUID();
        var amount = new BigDecimal("100000");
        var fee = new BigDecimal("1000");
        var initialBalance = new BigDecimal("500000");

        var from = new Account(fromId, "111", initialBalance);
        var to = new Account(toId, "222", new BigDecimal("200000"));

        given(accountPort.findById(fromId)).willReturn(Optional.of(from));
        given(accountPort.findById(toId)).willReturn(Optional.of(to));
        given(transactionPort.sumTransferredToday(eq(fromId), any())).willReturn(BigDecimal.ZERO);

        // when
        transferService.transfer(fromId, toId, amount);

        // then
        verify(transferLimitPolicy).validate(BigDecimal.ZERO, amount);
        verify(accountPort).save(from);
        verify(accountPort).save(to);
        verify(transactionPort).saveTransfer(
                eq(fromId),
                eq(toId),
                eq(amount),
                argThat(it -> it.compareTo(fee) == 0)
        );
        assertEquals(0, from.getBalance().compareTo(new BigDecimal("399000")));
        assertEquals(0, to.getBalance().compareTo(new BigDecimal("300000")));
    }

    @Test
    void 동일한_계좌로는_이체할_수_없다() {
        var accountId = UUID.randomUUID();
        var amount = new BigDecimal("100000");

        assertThrows(IllegalArgumentException.class, () ->
                transferService.transfer(accountId, accountId, amount)
        );
    }

    @Test
    void 잔액이_부족하면_이체할_수_없다() {
        var fromId = UUID.randomUUID();
        var toId = UUID.randomUUID();
        var amount = new BigDecimal("1000000"); // 너무 많음

        var from = new Account(fromId, "111", new BigDecimal("1000")); // 잔액 부족
        var to = new Account(toId, "222", new BigDecimal("0"));

        given(accountPort.findById(fromId)).willReturn(Optional.of(from));
        given(accountPort.findById(toId)).willReturn(Optional.of(to));
        given(transactionPort.sumTransferredToday(eq(fromId), any())).willReturn(BigDecimal.ZERO);

        assertThrows(InsufficientBalanceException.class, () ->
                transferService.transfer(fromId, toId, amount)
        );
    }

    @Test
    void 이체한도를_초과하면_예외가_발생한다() {
        var fromId = UUID.randomUUID();
        var toId = UUID.randomUUID();
        var amount = new BigDecimal("100000");

        var from = new Account(fromId, "111", new BigDecimal("9999999"));
        var to = new Account(toId, "222", new BigDecimal("0"));

        given(accountPort.findById(fromId)).willReturn(Optional.of(from));
        given(accountPort.findById(toId)).willReturn(Optional.of(to));
        given(transactionPort.sumTransferredToday(eq(fromId), any())).willReturn(new BigDecimal("2990000"));

        doThrow(new IllegalArgumentException("이체 한도 초과"))
                .when(transferLimitPolicy).validate(any(), any());

        assertThrows(IllegalArgumentException.class, () ->
                transferService.transfer(fromId, toId, amount)
        );
    }

}

