package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto addUser(UserDtoRequest requestDto) {
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUserFromRequest(requestDto)));
        } catch (DuplicateEmailException e) {
            throw new DuplicateEmailException(
                    String.format("Пользователь с email %s уже существует", requestDto.getEmail()));
        }
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %s не найден", id)));
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null) {
            user.setName(name);
        }
        if (email != null) {
            user.setEmail(email);
        }
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (DuplicateEmailException e) {
            throw new DuplicateEmailException(
                    String.format("Пользователь с email %s уже существует", userDto.getEmail()));
        }
    }

    @Override
    public UserDto getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %s не найден", id)));
        return UserMapper.toUserDto(user);
    }

    @Transactional
    @Override
    public void deleteUserById(long id) {
        getUserById(id);
        userRepository.deleteById(id);
    }
}