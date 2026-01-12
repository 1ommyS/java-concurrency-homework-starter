package ru.homework.vt;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Запуск пачки блокирующих задач на виртуальных потоках с ограничением параллелизма.
 */
public final class BatchRunner {
    private final Work work;

    public BatchRunner(Work work) {
        this.work = Objects.requireNonNull(work);
    }

    /**
     * TODO:
     * 1) Запускай задачи на virtual threads (Executors.newVirtualThreadPerTaskExecutor())
     * 2) Ограничь параллелизм через Semaphore(maxParallelism)
     * 3) Верни Map id -> результат
     * 4) Если любая задача упала — брось исключение
     */
    public Map<String, String> runAll(List<String> ids, int maxParallelism) {
        throw new UnsupportedOperationException("TODO: реализуй runAll");
    }
}
