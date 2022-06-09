package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.Collection;

public interface EventsStorage {
    /**
     * Возращает историю действий друзей пользователя
     * @param id уникальный идентификатор пользователя
     * @return список истории
     */
    Collection<Event> getEvents(Long id);
}

