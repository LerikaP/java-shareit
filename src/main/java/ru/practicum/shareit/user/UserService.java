package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getAllUsers();

    UserDto addUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, int id);

    UserDto getUserById(int id);

    void deleteUserById(int id);
}
