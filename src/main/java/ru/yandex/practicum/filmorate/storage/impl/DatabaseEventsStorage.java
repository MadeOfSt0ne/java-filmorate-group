package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventsStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseEventsStorage implements EventsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Event> getEvents(Long id) {
        List<Event> list = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate
                .queryForRowSet("SELECT * FROM events e \n" +
                        "JOIN users u ON e.user_id = u.user_id \n" +
                        "JOIN friends f ON u.user_id = f.friend_id  \n" +
                        "WHERE f.user_id  = ? \n", id);
        while (sqlRowSet.next()) {
            list.add(Event.builder()
                    .eventId(sqlRowSet.getLong("event_id"))
                    .userId(sqlRowSet.getLong("user_id"))
                    .eventType(sqlRowSet.getString("event_type"))
                    .operation(sqlRowSet.getString("operation"))
                    .timestamp(sqlRowSet.getLong("time_stamp"))
                    .entityId(sqlRowSet.getLong("entity_id")).build());

            Comparator<Event> mapComparator = Comparator.comparing(Event::getTimestamp).reversed();
            list.sort(mapComparator);
        }
        return list;
    }
}
