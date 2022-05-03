package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;
    public void addFriends(Long id, Long friendId) {
        User user1 = storage.get(id);
        User user2 = storage.get(friendId);

        if (user1 == null || user2 == null) throw new NoSuchElementException();

        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
    }

    public void deleteFriends(Long id, Long friendId) {
        User user1 = storage.get(id);
        User user2 = storage.get(friendId);

        if (user1 == null || user2 == null) throw new NoSuchElementException();

        user1.getFriends().remove(user2.getId());
        user2.getFriends().remove(user1.getId());
    }

    public Collection<User> getFriendsIntersection(Long id, Long otherId) {
        User user1 = storage.get(id);
        User user2 = storage.get(otherId);

        if (user1 == null || user2 == null) throw new NoSuchElementException();

        Set<Long> intersection = new HashSet<>(user1.getFriends());
        intersection.retainAll(user2.getFriends());

        return intersection.stream().map(storage::get).collect(Collectors.toList());
    }
}
