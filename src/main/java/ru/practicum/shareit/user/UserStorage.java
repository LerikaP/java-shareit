package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Component
public interface UserStorage {

    List<User> getAllUsers();

    User addUser(User user);

    User updateUser(User user);

    User getUserById(long id);

    void deleteUserById(long id);
}
