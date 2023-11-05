package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import java.util.List;

@Service
public interface UserService {

    List<UserDto> getAllUsers();

    UserDto addUser(UserDtoRequest requestDto);

    UserDto updateUser(UserDto userDto, long id);

    UserDto getUserById(long id);

    void deleteUserById(long id);
}
