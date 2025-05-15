package com.wirebarley.cache;

import com.wirebarley.api.ApiApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = ApiApplication.class) // ← 당신의 @SpringBootApplication 클래스
@Import({RedisLockAspect.class, RedisLockTest.FakeService.class})
class RedisLockTest {

    @MockBean
    RedisLockPort redisLockPort;

    @Autowired
    FakeService fakeService;

    @Test
    void 락이_정상적으로_획득되면_메서드가_실행된다() {
        // given
        given(redisLockPort.lock(eq("lock:account:test-id"), any()))
                .willReturn(true);

        // when
        fakeService.doSomething("test-id");

        // then
        verify(redisLockPort).lock(eq("lock:account:test-id"), any());
        verify(redisLockPort).unlock("lock:account:test-id");
    }

    @Test
    void 락을_획득하지_못하면_예외를_던진다() {
        // given
        given(redisLockPort.lock(any(), any()))
                .willReturn(false);

        // expect
        assertThatThrownBy(() -> fakeService.doSomething("test-id"))
                .isInstanceOf(RedisLockException.class)
                .hasMessageContaining("처리 중입니다. 잠시 후 다시 시도해주세요");
    }

    @Component
    public static class FakeService {

        @RedisLock(prefix = "lock:account:", key = "#id")
        public void doSomething(String id) {
            // 실제 서비스처럼 동작하는 가짜 로직
        }
    }

}

