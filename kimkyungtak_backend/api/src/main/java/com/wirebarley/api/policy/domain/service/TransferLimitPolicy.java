package com.wirebarley.api.policy.domain.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransferLimitPolicy {

    private static final BigDecimal DAILY_LIMIT = new BigDecimal("3000000");

    public void validate(BigDecimal todayTotal, BigDecimal newAmount) {
        var total = todayTotal.add(newAmount);
        if (total.compareTo(DAILY_LIMIT) > 0) {
            throw new IllegalArgumentException("1일 이체 한도(3,000,000원)를 초과했습니다.");
        }
    }
}
