package ru.homework.cf.service;

import ru.homework.cf.model.Order;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Заглушка "внешнего" сервиса заказов.
 *
 * Важно: метод блокирующий (sleep) — имитируем сеть/БД.
 */
public final class OrdersService {
    private final Duration delay;
    private final CountDownLatch startedLatch;
    private final CountDownLatch proceedLatch;

    public OrdersService(Duration delay) {
        this(delay, null, null);
    }

    public OrdersService(Duration delay, CountDownLatch startedLatch, CountDownLatch proceedLatch) {
        this.delay = Objects.requireNonNull(delay);
        this.startedLatch = startedLatch;
        this.proceedLatch = proceedLatch;
    }

    public List<Order> fetchOrders(String userId) {
        if (startedLatch != null) {
            startedLatch.countDown();
        }

        if (proceedLatch != null) {
            try {
                boolean ok = proceedLatch.await(2, TimeUnit.SECONDS);
                if (!ok) {
                    throw new IllegalStateException("Параллельность не достигнута: не дождались proceedLatch");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }

        sleep(delay);

        if (userId.contains("fail-orders")) {
            throw new RuntimeException("Orders service failed");
        }

        // Детерминированные данные, чтобы тесты были стабильны
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("o1-" + userId, 100));
        orders.add(new Order("o2-" + userId, 250));
        return orders;
    }

    private static void sleep(Duration d) {
        try {
            Thread.sleep(d.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
