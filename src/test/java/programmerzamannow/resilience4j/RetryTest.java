package programmerzamannow.resilience4j;

import io.github.resilience4j.retry.Retry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class RetryTest {

  void callMe(){
    log.info("Try call me");
    throw new IllegalArgumentException("Ups error");
  }

  @Test
  void createNewRetry() {
    Retry retry = Retry.ofDefaults("pzn");

    Runnable runnable = Retry.decorateRunnable(retry, () -> callMe());
    runnable.run();
  }

  String hello(){
    log.info("Call say hello");
    throw new IllegalArgumentException("Ups error say hello");
  }

  @Test
  void createRetrySupplier() {
    Retry retry = Retry.ofDefaults("pzn");

    Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hello());
    supplier.get();
  }
}
