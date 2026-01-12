package ru.homework.cf;

import org.junit.jupiter.api.Test;
import ru.homework.cf.model.UserSummary;
import ru.homework.cf.service.OrdersService;
import ru.homework.cf.service.ProfileService;

import java.time.Duration;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class UserSummaryServiceTest {

    @Test
    void should_build_summary_and_run_in_parallel() throws Exception {
        // Ловим "параллельность" без измерения времени:
        // оба сервиса должны стартовать, прежде чем их отпустят дальше.
        CountDownLatch started = new CountDownLatch(2);
        CountDownLatch proceed = new CountDownLatch(1);

        ProfileService profileService = new ProfileService(Duration.ofMillis(50), started, proceed);
        OrdersService ordersService = new OrdersService(Duration.ofMillis(50), started, proceed);

        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            UserSummaryService svc = new UserSummaryService(profileService, ordersService, pool);

            CompletableFuture<UserSummary> f = svc.getUserSummary("42");

            // Ждём, что оба вызова реально запустились.
            boolean bothStarted = started.await(500, TimeUnit.MILLISECONDS);
            assertTrue(bothStarted, "Оба источника должны стартовать параллельно");

            // Теперь отпускаем выполнение
            proceed.countDown();

            UserSummary s = f.get(1, TimeUnit.SECONDS);
            assertEquals("42", s.userId());
            assertEquals("User-42", s.name());
            assertEquals(2, s.ordersCount());
            assertEquals(350, s.totalAmountRub());
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void should_propagate_error() {
        ProfileService profileService = new ProfileService(Duration.ofMillis(10));
        OrdersService ordersService = new OrdersService(Duration.ofMillis(10));

        ExecutorService pool = Executors.newFixedThreadPool(2);
        try {
            UserSummaryService svc = new UserSummaryService(profileService, ordersService, pool);
            CompletableFuture<UserSummary> f = svc.getUserSummary("fail-orders");
            ExecutionException ex = assertThrows(ExecutionException.class, () -> f.get(1, TimeUnit.SECONDS));
            assertNotNull(ex.getCause());
        } catch (TimeoutException | InterruptedException e) {
            fail(e);
        } finally {
            pool.shutdownNow();
        }
    }
}
