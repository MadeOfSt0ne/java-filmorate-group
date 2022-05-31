package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;

/**
 * Интерфейс для хранилища "дружбы" между пользователями. Подтвержденная дружба через взаимное добавление в друзья.
 */
public interface FriendshipStorage {
    /**
     * Получает друзей пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return коллекция друзей пользователя
     */
    Collection<Long> getUserFriendsIds(Long id);

    /**
     * Добавляет "дружбу" в хранилище.
     *
     * @param friendship "дружба"
     */
    void save(Friendship friendship);

    /**
     * Удаляет "дружбу" из хранилища.
     *
     * @param friendship "дружба"
     */
    void delete(Friendship friendship);
}
