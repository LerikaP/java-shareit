package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto addUser(UserDto userDto) {
        validateEmail(userDto);
        return UserMapper.toUserDto(userStorage.addUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        User user = userStorage.getUserById(id);
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            if (!user.getEmail().equals((userDto.getEmail()))) {
                validateEmail(userDto);
            }
            user.setEmail(email);
        }
        return UserMapper.toUserDto(userStorage.updateUser(user));
    }

    @Override
    public UserDto getUserById(int id) {
        return UserMapper.toUserDto(userStorage.getUserById(id));
    }

    @Override
    public void deleteUserById(int id) {
        userStorage.deleteUserById(id);
    }

    private void validateEmail(UserDto userDto) {
        if (userStorage.getAllUsers()
                .stream()
                .anyMatch(user -> (user.getEmail().equals(userDto.getEmail())))) {
            throw new DuplicateEmailException("Пользователь с указанным email уже существует");
        }
    }
}