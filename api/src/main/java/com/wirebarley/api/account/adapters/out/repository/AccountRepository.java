package com.wirebarley.api.account.adapters.out.repository;

import com.wirebarley.api.account.adapters.out.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    Boolean existsByAccountNumber(String accountNumber);
}
