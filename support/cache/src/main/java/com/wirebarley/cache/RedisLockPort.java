package com.wirebarley.cache;

import java.time.Duration;

public interface RedisLockPort {
    boolean lock(String key, Duration timeout);
    void unlock(String key);
}
