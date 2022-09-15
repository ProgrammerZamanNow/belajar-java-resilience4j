package programmerzamannow.resilience4j;

import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
public class TimeLimiterTest {

  @SneakyThrows
  public String slow(){
    log.info("Slow");
    Thread.sleep(5000L);
    return "Eko";
  }

  @Test
  void timeLimiter() throws Exception {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<String> future = executorService.submit(() -> slow());

    TimeLimiter timeLimiter = TimeLimiter.ofDefaults("pzn");
    Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);

    callable.call();
  }

  @Test
  void timeLimiterConfig() throws Exception {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<String> future = executorService.submit(() -> slow());

    TimeLimiterConfig config = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(10))
        .cancelRunningFuture(true)
        .build();

    TimeLimiter timeLimiter = TimeLimiter.of("pzn", config);
    Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);

    callable.call();
  }

  @Test
  void timeLimiterRegistry() throws Exception {
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<String> future = executorService.submit(() -> slow());

    TimeLimiterConfig config = TimeLimiterConfig.custom()
        .timeoutDuration(Duration.ofSeconds(10))
        .cancelRunningFuture(true)
        .build();

    TimeLimiterRegistry registry = TimeLimiterRegistry.ofDefaults();
    registry.addConfiguration("config", config);

    TimeLimiter timeLimiter = registry.timeLimiter("pzn", "config");
    Callable<String> callable = TimeLimiter.decorateFutureSupplier(timeLimiter, () -> future);

    callable.call();
  }
}
