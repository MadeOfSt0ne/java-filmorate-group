package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Реализация интерфейса хранилища "дружбы", с хранением в оперативной памяти.
 */
@Component
public class InMemoryFriendshipStorage implements FriendshipStorage {
    private final Map<Long, Set<Long>> friends = new HashMap<>();

    /**
     * Получает друзей пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return коллекция друзей пользователя
     */
    @Override
    public Set<Long> getUserFriendsIds(Long id) {
        return friends.getOrDefault(id, new HashSet<>());
    }

    /**
     * Добавляет "дружбу" в хранилище.
     *
     * @param friendship "дружба"
     */
    @Override
    public void save(Friendship friendship) {
        Set<Long> userFriends = getUserFriendsIds(friendship.getUser().getId());
        userFriends.add(friendship.getFriend().getId());
        friends.put(friendship.getUser().getId(), userFriends);
    }

    /**
     * Удаляет "дружбу" из хранилища.
     *
     * @param friendship "дружба"
     */
    @Override
    public void delete(Friendship friendship) {
        friends.get(friendship.getUser().getId()).remove(friendship.getFriend().getId());
    }
}
