package com.wirebarley.api.trasaction.adapters.out.repository;

import com.wirebarley.api.trasaction.adapters.out.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionEntity, Long> {
}
