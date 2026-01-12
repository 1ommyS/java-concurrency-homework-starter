# ДЗ по конкурентности в Java (упрощённая версия)

Тут **две маленькие задачи**:

1) `cf-simple` — **CompletableFuture**
2) `vt-simple` — **Virtual Threads**

Проект собран под **Java 21+**.

## Как запускать

```bash
mvn test
# или по модулю:
mvn test -pl cf-simple
mvn test -pl vt-simple
```

---

## 1) cf-simple — CompletableFuture: «Собери сводку пользователя»

### Сюжет
Есть два “сервиса” (заглушки уже есть в коде):

- `ProfileService` — отдаёт профиль пользователя
- `OrdersService` — отдаёт список заказов

Оба сервиса **блокирующие** (внутри `sleep`) — имитируют сеть/БД.

### Что нужно сделать
Реализуй метод:

- `UserSummaryService#getUserSummary(String userId)`

Он должен:

1. Запустить **параллельно** получение профиля и заказов (через `CompletableFuture`).
2. Если один из вызовов упал — итоговая сводка тоже должна упасть (проброс исключения).
3. Возвратить `UserSummary` (поля уже определены).
4. **Не использовать** `get()`/`join()` “в лоб” в середине (разрешено в самом конце в тестах/CLI).
5. Использовать отдельный `Executor` (не common pool), чтобы было очевидно управление потоками.

Подсказка: `thenCombine`, `supplyAsync`, `handle/exceptionally`, `allOf`.

Тесты: `UserSummaryServiceTest`.

---

## 2) vt-simple — Virtual Threads: «Скачай пачку задач с ограничением параллелизма»

### Сюжет
Есть “блокирующая” операция `BlockingWork#doWork(String id)` — внутри `sleep`.
Нужно выполнить много таких работ **эффективно** с помощью виртуальных потоков.

### Что нужно сделать
Реализуй метод:

- `BatchRunner#runAll(List<String> ids, int maxParallelism)`

Требования:

1. Для каждой `id` запускается отдельная работа `doWork(id)`.
2. Выполнение идёт на **виртуальных потоках** (`Executors.newVirtualThreadPerTaskExecutor()`).
3. Есть **лимит параллелизма** `maxParallelism` (например через `Semaphore`).
4. Верни `Map<String, String>` где ключ — `id`, значение — результат работы.
5. Если какая-то работа упала — метод должен бросить исключение (можно первое встретившееся).

Тесты: `BatchRunnerTest`.

---