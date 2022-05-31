package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * Реализация интерфейса хранилища пользователей, с хранением в реляционной базе данных.
 */
@Component
@RequiredArgsConstructor
public class DatabaseUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    /**
     * Получает всех пользователей из хранилища.
     *
     * @return коллекция пользователей
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public Collection<User> getAll() {
        final String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs));
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return пользователь или null если пользователя нет
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public User get(Long id) {
        final String sql = "SELECT * FROM users WHERE user_id = ? LIMIT 1";
        final List<User> users = jdbcTemplate.query(sql, (rs, rowNum) -> mapRowToUser(rs), id);
        return users.size() > 0 ? users.get(0) : null;
    }

    /**
     * Добавляет пользователя в хранилище.
     *
     * @param user пользователь
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void add(User user) {
        final String sql = "INSERT INTO users (user_id, email, login, name, birthday) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getId(), user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
    }

    /**
     * Обновляет пользователя в хранилище.
     *
     * @param user пользователь
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void update(User user) {
        final String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
    }

    /**
     * Удаляет пользователя из хранилища.
     *
     * @param user пользователь
     * @throws SQLException если SQL-запрос выполнился с ошибкой
     */
    @Override
    public void remove(User user) {
        final String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getId());
    }

    private User mapRowToUser(ResultSet rs) throws SQLException {
        return User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
