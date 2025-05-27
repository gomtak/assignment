package com.wirebarley.api.account.adapters.out.mapper;

import com.wirebarley.api.account.adapters.out.entity.AccountEntity;
import com.wirebarley.api.account.domain.model.Account;

public class AccountMapper {
    public static Account toDomain(AccountEntity entity) {
        return new Account(entity.getId(), entity.getAccountNumber(), entity.getBalance());
    }

    public static AccountEntity toEntity(Account account) {
        return new AccountEntity(account.getId(), account.getAccountNumber(), account.getBalance());
    }
}
