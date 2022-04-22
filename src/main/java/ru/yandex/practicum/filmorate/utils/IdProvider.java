package ru.yandex.practicum.filmorate.utils;

import java.util.HashMap;

/**
 * Утилитарный класс для генерации уникальных идентификаторов.
 */
public final class IdProvider {
    private static final HashMap<Object, Long> ids = new java.util.HashMap<>();

    /**
     * Генерирует последовательные уникальные идентификаторы по ключу из имени класса.
     *
     * @param className имя класс где нужен идентификатор
     * @return уникальный идентификатор
     */
    public static long getNextId(Object className) {
        long nextId = ids.getOrDefault(className, 0L) + 1;
        ids.put(className, nextId);
        return nextId;
    }
}
