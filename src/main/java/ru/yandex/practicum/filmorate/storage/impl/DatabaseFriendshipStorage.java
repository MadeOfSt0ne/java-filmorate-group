package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.sql.SQLException;
import java.time.Instant;
import java.util.Collection;

/**
 * Реализация интерфейса хранилища "дружбы", с хранением в реляционной базе данных.
 */
@Component
@RequiredArgsConstructor
public class DatabaseFriendshipStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получает друзей пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return коллекция друзей пользователя
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public Collection<Long> getUserFriendsIds(Long id) {
        final String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, numRow) -> rs.getLong("friend_id"), id);
    }

    /**
     * Добавляет "дружбу" в хранилище.
     *
     * @param friendship "дружба"
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void save(Friendship friendship) {
        final String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, friendship.getUser().getId(), friendship.getFriend().getId());
        addToHistory(friendship, EventType.FRIEND);
    }

    /**
     * Удаляет "дружбу" из хранилища.
     *
     * @param friendship "дружба"
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void delete(Friendship friendship) {
        final String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, friendship.getUser().getId(), friendship.getFriend().getId());
        addToHistory(friendship, EventType.FRIEND);
    }
    private void addToHistory(Friendship friendship, EventType eventType){
        jdbcTemplate.update("INSERT INTO history (USER_ID, EVENT_TYPE, TIME_STAMP, ENTITY_ID) VALUES (?, ?, ?, ?)",
                friendship.getUser().getId(), eventType.toString(), Instant.now().toEpochMilli(), friendship.getFriend().getId());
    }
}
