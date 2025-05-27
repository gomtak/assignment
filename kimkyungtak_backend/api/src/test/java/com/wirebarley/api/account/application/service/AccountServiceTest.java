package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountPort accountPort;

    @InjectMocks
    private AccountService accountService;

    @Test
    void 계좌를_생성한다() {
        // given
        var accountNumber = "1234567890";
        var initialBalance = new BigDecimal("100000");

        // when
        var resultId = accountService.createAccount(accountNumber, initialBalance);

        // then
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountPort).save(captor.capture());

        Account saved = captor.getValue();
        assertEquals(accountNumber, saved.getAccountNumber());
        assertEquals(0, saved.getBalance().compareTo(initialBalance));
        assertEquals(resultId, saved.getId());
    }

    @Test
    void 계좌를_삭제한다() {
        // given
        var id = UUID.randomUUID();

        // when
        accountService.deleteAccount(id);

        // then
        verify(accountPort).deleteById(id);
    }
}

