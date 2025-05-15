package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.in.AccountCreateUseCase;
import com.wirebarley.api.account.application.port.in.AccountDeleteUseCase;
import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class AccountService implements AccountCreateUseCase, AccountDeleteUseCase {
    private final AccountPort accountPort;

    public AccountService(AccountPort accountPort) {
        this.accountPort = accountPort;
    }

    @Override
    @Transactional
    public UUID createAccount(String accountNumber, BigDecimal initialBalance) {
        var id = UUID.randomUUID();
        var account = new Account(id, accountNumber, initialBalance);
        accountPort.save(account);
        return account.getId();
    }

    @Override
    public void deleteAccount(UUID accountId) {
        accountPort.deleteById(accountId);
    }
}
