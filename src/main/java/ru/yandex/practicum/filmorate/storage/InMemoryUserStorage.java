package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

/**
 * Реализация интерфейса хранилища пользователей, с хранением в оперативной памяти.
 */
@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Long, User> users = new HashMap<>();

    /**
     * Получает всех пользователей из хранилища.
     *
     * @return коллекция пользователей
     */
    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id уникальный идентификатор пользователя
     * @return пользователь или null если пользователя нет
     */
    @Override
    public User get(Long id) {
        return users.get(id);
    }

    /**
     * Добавляет пользователя в хранилище.
     *
     * @param user пользователь
     */
    @Override
    public void add(User user) {
        users.put(user.getId(), user);
    }

    /**
     * Обновляет пользователя в хранилище. По факту просто заменяет старого на нового.
     *
     * @param user пользователь
     */
    @Override
    public void update(User user) {
        users.put(user.getId(), user);
    }

    /**
     * Удаляет пользователя из хранилища.
     *
     * @param user пользователь
     */
    @Override
    public void remove(User user) {
        users.remove(user.getId());
    }

    /**
     * Очищает хранилище.
     */
    @Override
    public void clear() {
        users.clear();
    }
}
