package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.History;
import ru.yandex.practicum.filmorate.storage.HistoryStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseHistoryStorage implements HistoryStorage {
    private final JdbcTemplate jdbcTemplate;
    @Override
    public Collection<History> getHistory(Long id) {
        List<History> list = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate
                .queryForRowSet("SELECT h.event_id, h.user_id, h.event_type, h.time_stamp, h.entity_id \n" +
                        "FROM history h \n" +
                        "JOIN users u ON h.user_id = u.user_id \n" +
                        "JOIN friends f ON u.user_id = f.friend_id  \n" +
                        "WHERE f.user_id  = ? \n", id);
        while (sqlRowSet.next()) {
            list.add(History.builder()
                    .eventId(sqlRowSet.getLong("event_id"))
                    .userId(sqlRowSet.getLong("user_id"))
                    .eventType(sqlRowSet.getString("event_type"))
                    .timestamp(sqlRowSet.getLong("time_stamp"))
                    .entityId(sqlRowSet.getLong("entity_id")).build());

            Comparator<History> mapComparator = Comparator.comparing(History::getTimestamp).reversed();
            list.sort(mapComparator);
        }
        return list;
    }
}
