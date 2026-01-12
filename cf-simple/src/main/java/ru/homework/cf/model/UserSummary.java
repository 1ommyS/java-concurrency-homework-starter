package ru.homework.cf.model;

/**
 * Итоговая сводка по пользователю.
 */
public record UserSummary(
        String userId,
        String name,
        int ordersCount,
        long totalAmountRub
) {}
