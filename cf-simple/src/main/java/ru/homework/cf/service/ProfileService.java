package ru.homework.cf.service;

import ru.homework.cf.model.UserProfile;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Заглушка "внешнего" сервиса профилей.
 *
 * Важно: метод блокирующий (sleep) — имитируем сеть/БД.
 * В тестах можно передать latch-и, чтобы проверить параллельность без тайминговых фокусов.
 */
public final class ProfileService {
    private final Duration delay;
    private final CountDownLatch startedLatch;
    private final CountDownLatch proceedLatch;

    public ProfileService(Duration delay) {
        this(delay, null, null);
    }

    public ProfileService(Duration delay, CountDownLatch startedLatch, CountDownLatch proceedLatch) {
        this.delay = Objects.requireNonNull(delay);
        this.startedLatch = startedLatch;
        this.proceedLatch = proceedLatch;
    }

    public UserProfile fetchProfile(String userId) {
        // Сигнал "я стартовал" (для тестов)
        if (startedLatch != null) {
            startedLatch.countDown();
        }

        // В тестах можем "зажать" выполнение, чтобы дождаться старта второго вызова
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

        if (userId.contains("fail-profile")) {
            throw new RuntimeException("Profile service failed");
        }

        return new UserProfile(userId, "User-" + userId);
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
