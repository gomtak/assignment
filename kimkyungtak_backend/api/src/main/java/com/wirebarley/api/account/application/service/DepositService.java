package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.in.DepositUseCase;
import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.trasaction.application.port.out.TransactionPort;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class DepositService implements DepositUseCase {

    private final AccountPort accountPort;
    private final TransactionPort transactionPort;

    public DepositService(AccountPort accountPort, TransactionPort transactionPort) {
        this.accountPort = accountPort;
        this.transactionPort = transactionPort;
    }

    @Override
    @Transactional
    public void deposit(UUID accountId, BigDecimal amount) {
        var account = accountPort.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        account.deposit(amount);
        accountPort.save(account);
        transactionPort.saveDeposit(accountId, amount);
    }
}
