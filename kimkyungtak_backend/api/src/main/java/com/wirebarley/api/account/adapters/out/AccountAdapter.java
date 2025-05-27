package com.wirebarley.api.account.adapters.out;

import com.wirebarley.api.account.adapters.out.entity.AccountEntity;
import com.wirebarley.api.account.adapters.out.mapper.AccountMapper;
import com.wirebarley.api.account.adapters.out.repository.AccountRepository;
import com.wirebarley.api.account.application.port.out.AccountPort;
import com.wirebarley.api.account.domain.model.Account;
import com.wirebarley.api.common.annotation.PersistenceAdapter;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@PersistenceAdapter
public class AccountAdapter implements AccountPort {

    private final AccountRepository accountRepository;

    public AccountAdapter(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Optional<Account> findById(UUID accountId) {
        return accountRepository.findById(accountId)
                .map(AccountMapper::toDomain);

    }

    @Override
    @Transactional
    public UUID save(Account account) {
        AccountEntity saved = accountRepository.save(AccountMapper.toEntity(account));
        return saved.getId();
    }

    @Override
    @Transactional
    public void deleteById(UUID accountId) {
        accountRepository.deleteById(accountId);
    }

    @Override
    public Boolean existsAccountNumber(String accountNumber) {
        return accountRepository.existsByAccountNumber(accountNumber);
    }
}
