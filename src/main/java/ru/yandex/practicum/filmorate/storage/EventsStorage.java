package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperations;
import ru.yandex.practicum.filmorate.model.EventType;

import java.util.Collection;

public interface EventsStorage {
    /**
     * Возращает историю действий друзей пользователя.
     *
     * @param id уникальный идентификатор пользователя
     * @return список истории
     */
    Collection<Event> getEvents(Long id);

    /**
     * Сохраняет событие в таблицу events.
     *
     * @param object    Сущьность с которой произошло событие
     * @param eventType Тип события
     * @param operation Подтип события
     */
    void add(Object object, EventType eventType, EventOperations operation);
}
