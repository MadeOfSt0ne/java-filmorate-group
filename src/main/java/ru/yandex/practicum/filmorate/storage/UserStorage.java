package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Интерфейс для хранилища пользователей.
 */
public interface UserStorage {
    /**
     * Получает всех пользователей из хранилища.
     *
     * @return коллекция пользователей
     */
    Collection<User> getAll();

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return пользователь или null если пользователя нет
     */
    User get(Long id);

    /**
     * Добавляет пользователя в хранилище.
     *
     * @param user пользователь
     */
    void add(User user);

    /**
     * Обновляет пользователя в хранилище.
     *
     * @param user пользователь
     */
    void update(User user);

    /**
     * Удаляет пользователя из хранилища.
     *
     * @param user пользователь
     */
    void remove(User user);
}
