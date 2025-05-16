package com.wirebarley.api.account.application.service;

import com.wirebarley.api.account.application.port.in.AccountCreateUseCase;
import com.wirebarley.api.account.application.port.in.AccountDeleteUseCase;
import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.exception.ExistsAccountNumberException;
import com.wirebarley.api.account.domain.model.Account;
import jakarta.persistence.EntityNotFoundException;
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
        // 계좌번호 중복 체크
        if (accountPort.existsAccountNumber(accountNumber)) {
            throw new ExistsAccountNumberException("이미 존재하는 계좌번호입니다.");
        }
        var account = new Account(null, accountNumber, initialBalance);
        return accountPort.save(account);
    }

    @Override
    public void deleteAccount(UUID accountId) {
        Account account = accountPort.findById(accountId)
                .orElseThrow(() -> new EntityNotFoundException("계좌가 존재하지 않습니다."));
        account.checkAccount();
        accountPort.deleteById(accountId);
    }
}
