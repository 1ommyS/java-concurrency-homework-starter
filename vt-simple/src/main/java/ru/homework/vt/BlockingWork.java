package ru.homework.vt;

import java.time.Duration;

/**
 * Простая блокирующая работа (sleep), чтобы показать пользу virtual threads.
 */
public final class BlockingWork implements Work {
    private final Duration delay;

    public BlockingWork(Duration delay) {
        this.delay = delay;
    }

    @Override
    public String doWork(String id) {
        try {
            Thread.sleep(delay.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        if (id.contains("fail")) {
            throw new RuntimeException("Work failed for id=" + id);
        }

        return "ok-" + id;
    }
}
