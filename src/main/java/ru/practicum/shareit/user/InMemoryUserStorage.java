package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int userIdGenerator = 1;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        int id = getNextFreeId();
        user.setId(id);
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public User updateUser(User user) {
        int id = user.getId();
        if (users.containsKey(id)) {
            users.put(id, user);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    @Override
    public User getUserById(int id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @Override
    public void deleteUserById(int id) {
        if (users.containsKey(id)) {
            users.remove(id);
        } else {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private int getNextFreeId() {
        return userIdGenerator++;
    }
}
