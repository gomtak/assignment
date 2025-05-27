package com.wirebarley.api.account.domain.model;

import com.wirebarley.api.account.domain.exception.InsufficientBalanceException;
import com.wirebarley.api.account.domain.exception.InvalidDepositAmountException;
import com.wirebarley.api.account.domain.exception.InvalidWithdrawAmountException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void deposit() {
        Account account = new Account(null, "1234567890", BigDecimal.ZERO);
        account.deposit(BigDecimal.valueOf(1000));
        assertEquals(BigDecimal.valueOf(1000), account.getBalance());
        account.deposit(BigDecimal.valueOf(500));
        assertEquals(BigDecimal.valueOf(1500), account.getBalance());
        assertThrows(InvalidDepositAmountException.class, () -> account.deposit(BigDecimal.valueOf(-100)));
        assertThrows(InvalidDepositAmountException.class, () -> account.deposit(BigDecimal.ZERO));
    }

    @Test
    void withdraw() {
        Account account = new Account(null, "1234567890", BigDecimal.valueOf(2000));
        account.withdraw(BigDecimal.valueOf(1000), BigDecimal.valueOf(100));
        assertEquals(BigDecimal.valueOf(900), account.getBalance());
        assertThrows(InvalidWithdrawAmountException.class, () -> account.withdraw(BigDecimal.valueOf(-100), BigDecimal.valueOf(100)));
        assertThrows(InvalidWithdrawAmountException.class, () -> account.withdraw(BigDecimal.ZERO, BigDecimal.valueOf(100)));
        assertThrows(InsufficientBalanceException.class, () -> account.withdraw(BigDecimal.valueOf(2000), BigDecimal.valueOf(100)));
    }
}
