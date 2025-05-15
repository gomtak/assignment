package com.wirebarley.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Aspect
@Component
public class RedisLockAspect {

    private final RedisLockPort redisLockPort;

    public RedisLockAspect(RedisLockPort redisLockPort) {
        this.redisLockPort = redisLockPort;
    }

    @Around("@annotation(redisLock)")
    public Object applyRedisLock(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        String key = getKey(redisLock, joinPoint);
        Duration timeout = Duration.ofSeconds(redisLock.timeoutSeconds());
        boolean locked = redisLockPort.lock(key, timeout);
        if (!locked) {
            throw new RedisLockException("처리 중입니다. 잠시 후 다시 시도해주세요");
        }

        try {
            return joinPoint.proceed();
        } finally {
            redisLockPort.unlock(key);
        }
    }

    private String getKey(RedisLock redisLock, ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        EvaluationContext context = new StandardEvaluationContext();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], args[i]);
        }

        ExpressionParser parser = new SpelExpressionParser();
        String evaluatedKey = parser.parseExpression(redisLock.key()).getValue(context, String.class);

        return redisLock.prefix() + evaluatedKey;
    }
}

