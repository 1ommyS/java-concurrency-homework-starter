package ru.homework.cf;

import ru.homework.cf.model.UserSummary;
import ru.homework.cf.service.OrdersService;
import ru.homework.cf.service.ProfileService;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Сервис, который собирает сводку пользователя из двух блокирующих источников
 * с помощью CompletableFuture.
 */
public final class UserSummaryService {
    private final ProfileService profileService;
    private final OrdersService ordersService;
    private final Executor executor;

    public UserSummaryService(ProfileService profileService, OrdersService ordersService, Executor executor) {
        this.profileService = Objects.requireNonNull(profileService);
        this.ordersService = Objects.requireNonNull(ordersService);
        this.executor = Objects.requireNonNull(executor);
    }

    /**
     * TODO:
     * 1) Запусти параллельно запрос профиля и заказов (CompletableFuture.supplyAsync)
     * 2) Объедини результат в UserSummary (thenCombine)
     * 3) Исключения пробрасывай наружу
     * 4) Используй переданный executor (не common pool)
     */
    public CompletableFuture<UserSummary> getUserSummary(String userId) {
        return CompletableFuture.failedFuture(new UnsupportedOperationException("TODO: реализуй getUserSummary"));
    }
}
