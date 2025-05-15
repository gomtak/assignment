package com.wirebarley.api.account.domain.model;

import com.wirebarley.api.account.domain.exception.InsufficientBalanceException;
import com.wirebarley.api.account.domain.exception.InvalidDepositAmountException;
import com.wirebarley.api.account.domain.exception.InvalidWithdrawAmountException;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Account {

    private final UUID id;
    private final String accountNumber;
    private BigDecimal balance;

    public Account(UUID id, String accountNumber, BigDecimal balance) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidDepositAmountException(amount);
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount, BigDecimal fee) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidWithdrawAmountException(amount);
        }
        BigDecimal totalAmount = amount.add(fee);
        if (this.balance.compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException(id, balance, totalAmount);
        }
        this.balance = this.balance.subtract(totalAmount);
    }
}
