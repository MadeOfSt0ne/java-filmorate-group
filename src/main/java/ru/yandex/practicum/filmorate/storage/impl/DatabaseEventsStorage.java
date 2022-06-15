package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.EventsStorage;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseEventsStorage implements EventsStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Возращает историю действий друзей пользователя.
     *
     * @param id уникальный идентификатор пользователя
     * @return список истории
     */
    @Override
    public Collection<Event> getEvents(Long id) {
        List<Event> list = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate
                .queryForRowSet("SELECT e.EVENT_ID, e.TIME_STAMP, e.USER_ID, e.EVENT_TYPE, e.EVENT_OPERATION, e.ENTITY_ID " +
                        "FROM events e \n" +
                        "JOIN users u ON e.user_id = u.user_id\n" +
                        "JOIN friends f ON u.user_id = f.friend_id\n" +
                        "WHERE f.user_id  = ?", id);
        while (sqlRowSet.next()) {
            list.add(Event.builder()
                    .eventId(sqlRowSet.getLong("event_id"))
                    .userId(sqlRowSet.getLong("user_id"))
                    .eventType(sqlRowSet.getString("event_type"))
                    .operation(sqlRowSet.getString("event_operation"))
                    .timestamp(sqlRowSet.getLong("time_stamp"))
                    .entityId(sqlRowSet.getLong("entity_id")).build());

            Comparator<Event> mapComparator = Comparator.comparing(Event::getTimestamp).reversed();
            list.sort(mapComparator);
        }
        return list;
    }

    /**
     * Сохраняет событие в таблицу events.
     *
     * @param object    Сущьность с которой произошло событие
     * @param eventType Тип события
     * @param operation Подтип события
     */
    @Override
    public void add(Object object, EventType eventType, EventOperations operation) {
        if (object instanceof Like) {
            jdbcTemplate.update("INSERT INTO events (USER_ID, EVENT_TYPE, EVENT_OPERATION, TIME_STAMP, ENTITY_ID) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    ((Like) object).getUser().getId(),
                    eventType.toString(),
                    operation.toString(),
                    Instant.now().toEpochMilli(),
                    ((Like) object).getFilm().getId());
        } else if (object instanceof Friendship) {
            jdbcTemplate.update("INSERT INTO events (USER_ID, EVENT_TYPE, EVENT_OPERATION, TIME_STAMP, ENTITY_ID) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    ((Friendship) object).getUser().getId(),
                    eventType.toString(),
                    operation.toString(),
                    Instant.now().toEpochMilli(),
                    ((Friendship) object).getFriend().getId());
        } else if (object instanceof Review) {
            jdbcTemplate.update("INSERT INTO events (USER_ID, EVENT_TYPE, EVENT_OPERATION, TIME_STAMP, ENTITY_ID) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    ((Review) object).getUserId(),
                    eventType.toString(),
                    operation.toString(),
                    Instant.now().toEpochMilli(),
                    ((Review) object).getFilmId());
        }
    }
}
