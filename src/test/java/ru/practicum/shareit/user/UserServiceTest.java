package ru.practicum.shareit.user;

import org.junit.jupiter.api.*;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceTest {
    private static UserService userService;
    private static UserRepository userRepository;
    private static UserDto userDto;
    private static User user;
    private static UserDtoRequest userDtoRequest;


    @BeforeAll
    static void beforeAll() {
        userDto = new UserDto(1L, "user 1", "user1@email.com");
        user = new User(1L, "user 1", "user1@email.com");
        userDtoRequest = new UserDtoRequest("user 1", "user1@email.com");
    }

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    @Order(value = 1)
    void should_create_user() {
        when(userRepository.save(any(User.class)))
                .thenReturn(user);
        UserDto userDtoForTest = userService.addUser(userDtoRequest);

        assertThat(userDtoForTest)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", userDtoRequest.getName())
                .hasFieldOrPropertyWithValue("email", userDtoRequest.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @Order(value = 2)
    void should_find_all_users() {
        final List<User> users = new ArrayList<>(Collections.singletonList(user));
        when(userRepository.findAll())
                .thenReturn(users);

        final List<UserDto> userDtos = userService.getAllUsers();

        assertThat(userDtos)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", user.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", user.getName());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", user.getEmail());
                });
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @Order(value = 3)
    void should_update_user() {
        user.setName("new user 1");
        UserDto newUserDto = new UserDto(1L, "new user 1", "user1@email.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenReturn(user);

        UserDto userDtoForTest = userService.updateUser(newUserDto, userDto.getId());

        assertThat(userDtoForTest)
                .hasFieldOrPropertyWithValue("id", newUserDto.getId())
                .hasFieldOrPropertyWithValue("name", newUserDto.getName())
                .hasFieldOrPropertyWithValue("email", newUserDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @Order(value = 4)
    void should_not_add_user_with_duplicate_email() {
        when(userRepository.save(any(User.class)))
                .thenThrow(new DuplicateEmailException("Пользователь с указанным email уже существует"));

        final DuplicateEmailException e = Assertions.assertThrows(
                DuplicateEmailException.class, () -> userService.addUser(userDtoRequest));

        assertEquals(String.format("Пользователь с email %s уже существует", userDto.getEmail()), e.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @Order(value = 5)
    void should_not_update_user_with_duplicate_email() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenThrow(new DuplicateEmailException("Пользователь с указанным email уже существует"));

        final DuplicateEmailException e = Assertions.assertThrows(
                DuplicateEmailException.class, () -> userService.updateUser(userDto, userDto.getId()));

        assertEquals(String.format("Пользователь с email %s уже существует", userDto.getEmail()), e.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }


}
