package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        return userClient.getUsers();
    }

    @GetMapping("{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        log.info("Get user {}", userId);
        return userClient.getUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Validated(Create.class) @RequestBody UserRequestDto requestDto) {
        log.info("Creating user {}", requestDto);
        return userClient.addUser(requestDto);
    }

    @PatchMapping("{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @Validated(Update.class) @RequestBody UserRequestDto requestDto) {
        log.info("Update user {}, userId={}", requestDto, userId);
        return userClient.updateUser(userId, requestDto);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable long userId) {
        log.info("Delete user {}", userId);
        return userClient.deleteUser(userId);
    }
}
