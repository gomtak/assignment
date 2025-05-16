package com.wirebarley.api.account.application.port.out;

import com.wirebarley.api.account.domain.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountPort {
    Optional<Account> findById(UUID accountId);
    UUID save(Account account);
    void deleteById(UUID accountId);

    Boolean existsAccountNumber(String accountNumber);
}
