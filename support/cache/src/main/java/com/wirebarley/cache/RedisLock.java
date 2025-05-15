package com.wirebarley.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    String prefix();               // 예: "lock:account:"
    String key();                  // SpEL: "#accountId"
    long timeoutSeconds() default 3;
}

