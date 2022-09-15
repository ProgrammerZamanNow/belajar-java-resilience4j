package programmerzamannow.resilience4j;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

@Slf4j
public class EventPublisherTest {

  @Test
  void retry() {
    Retry retry = Retry.ofDefaults("pzn");
    retry.getEventPublisher().onRetry(event -> {
      log.info("try to retry");
    });

    try {
      Supplier<String> supplier = Retry.decorateSupplier(retry, () -> hello());
      supplier.get();
    } catch (Exception e) {
      System.out.println(retry.getMetrics().getNumberOfFailedCallsWithRetryAttempt());
      System.out.println(retry.getMetrics().getNumberOfFailedCallsWithoutRetryAttempt());
      System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithRetryAttempt());
      System.out.println(retry.getMetrics().getNumberOfSuccessfulCallsWithoutRetryAttempt());
    }
  }

  private String hello() {
    throw new IllegalArgumentException("Ups");
  }

  @Test
  void registry() {
    RetryRegistry registry = RetryRegistry.ofDefaults();
    registry.getEventPublisher().onEntryAdded(event -> {
      log.info("Add new entry {}", event.getAddedEntry().getName());
    });

    registry.retry("pzn");
    registry.retry("pzn");
    registry.retry("pzn2");
  }
}
