package programmerzamannow.resilience4j;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RateLimiterTest {

  private final AtomicLong counter = new AtomicLong(0L);

  @Test
  void testRateLimiter() {
    RateLimiter rateLimiter = RateLimiter.ofDefaults("pzn");

    for (int i = 0; i < 10_000; i++) {
      Runnable runnable = RateLimiter.decorateRunnable(rateLimiter, () -> {
        long result = counter.incrementAndGet();
        log.info("Result: {}", result);
      });

      runnable.run();
    }
  }

  @Test
  void testRateLimiterConfig() {
    RateLimiterConfig config = RateLimiterConfig.custom()
        .limitForPeriod(100)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .timeoutDuration(Duration.ofSeconds(2))
        .build();

    RateLimiter rateLimiter = RateLimiter.of("pzn", config);

    for (int i = 0; i < 10_000; i++) {
      Runnable runnable = RateLimiter.decorateRunnable(rateLimiter, () -> {
        long result = counter.incrementAndGet();
        log.info("Result: {}", result);
      });

      runnable.run();
    }
  }

  @Test
  void testRateLimiterRegistry() {
    RateLimiterConfig config = RateLimiterConfig.custom()
        .limitForPeriod(100)
        .limitRefreshPeriod(Duration.ofMinutes(1))
        .timeoutDuration(Duration.ofSeconds(2))
        .build();

    RateLimiterRegistry registry = RateLimiterRegistry.ofDefaults();
    registry.addConfiguration("config", config);

    RateLimiter rateLimiter = registry.rateLimiter("pzn", "config");

    for (int i = 0; i < 10_000; i++) {
      Runnable runnable = RateLimiter.decorateRunnable(rateLimiter, () -> {
        long result = counter.incrementAndGet();
        log.info("Result: {}", result);
      });

      runnable.run();
    }
  }
}
