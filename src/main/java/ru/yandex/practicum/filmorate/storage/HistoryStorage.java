package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.History;

import java.util.Collection;

public interface HistoryStorage {
    /**
     * Возращает историю действий друзей пользователя
     * @param id уникальный идентификатор пользователя
     * @return список истории
     */
    Collection<History> getHistory(Long id);
}

