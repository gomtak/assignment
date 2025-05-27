package com.wirebarley.cache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisLockAdapter implements RedisLockPort {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisLockAdapter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean lock(String key, Duration timeout) {
        var result = redisTemplate.opsForValue().setIfAbsent(key, "locked", timeout);
        return Boolean.TRUE.equals(result);
    }

    @Override
    public void unlock(String key) {
        redisTemplate.delete(key);
    }
}
