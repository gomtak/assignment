package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.model.Account;
import com.wirebarley.api.policy.domain.service.DailyWithdrawLimitPolicy;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WithdrawServiceTest {

    @Mock
    private AccountPort accountPort;

    @Mock
    private TransactionPort transactionPort;

    @Mock
    private DailyWithdrawLimitPolicy dailyWithdrawLimitPolicy;

    @InjectMocks
    private WithdrawService withdrawService;

    @Test
    void 정상적으로_출금이_수행된다() {
        // given
        var accountId = UUID.randomUUID();
        var balance = new BigDecimal("1000000");
        var amount = new BigDecimal("100000");

        var account = new Account(accountId, "1234567890", balance);

        given(accountPort.findById(accountId)).willReturn(Optional.of(account));
        given(transactionPort.sumWithdrawnToday(eq(accountId), any())).willReturn(BigDecimal.ZERO);

        // when
        withdrawService.withdraw(accountId, amount);

        // then
        verify(dailyWithdrawLimitPolicy).validate(BigDecimal.ZERO, amount);
        verify(accountPort).save(account);
        verify(transactionPort).saveWithdrawal(accountId, amount);
        assertEquals(new BigDecimal("900000"), account.getBalance());
    }
}
