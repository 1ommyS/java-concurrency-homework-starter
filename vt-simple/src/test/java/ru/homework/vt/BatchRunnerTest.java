package ru.homework.vt;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class BatchRunnerTest {

    @Test
    void should_respect_max_parallelism_and_use_virtual_threads() {
        AtomicInteger active = new AtomicInteger(0);
        AtomicInteger max = new AtomicInteger(0);
        AtomicBoolean allVirtual = new AtomicBoolean(true);

        Work w = id -> {
            // проверяем, что задача реально выполняется на виртуальном потоке
            if (!Thread.currentThread().isVirtual()) {
                allVirtual.set(false);
            }

            int now = active.incrementAndGet();
            max.accumulateAndGet(now, Math::max);

            try {
                Thread.sleep(30); // блокирующее ожидание
            } finally {
                active.decrementAndGet();
            }

            return "ok-" + id;
        };

        BatchRunner runner = new BatchRunner(w);

        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 20; i++) ids.add("id-" + i);

        Map<String, String> res = runner.runAll(ids, 3);

        assertEquals(20, res.size());
        assertTrue(allVirtual.get(), "Ожидали выполнение на виртуальных потоках");
        assertTrue(max.get() <= 3, "Ожидали, что параллелизм не превысит 3, но было " + max.get());
    }

    @Test
    void should_fail_if_any_task_fails() {
        Work w = id -> {
            if (id.equals("bad")) throw new RuntimeException("boom");
            return "ok-" + id;
        };

        BatchRunner runner = new BatchRunner(w);

        List<String> ids = List.of("a", "bad", "c");
        assertThrows(RuntimeException.class, () -> runner.runAll(ids, 2));
    }
}
