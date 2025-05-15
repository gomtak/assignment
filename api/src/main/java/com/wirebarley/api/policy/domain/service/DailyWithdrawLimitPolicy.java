package com.wirebarley.api.policy.domain.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DailyWithdrawLimitPolicy {

    private static final BigDecimal DAILY_LIMIT = new BigDecimal("1000000");

    public void validate(BigDecimal totalToday, BigDecimal newRequest) {
        var newTotal = totalToday.add(newRequest);
        if (newTotal.compareTo(DAILY_LIMIT) > 0) {
            throw new IllegalArgumentException("1일 출금 한도(100만원)를 초과했습니다.");
        }
    }
}
