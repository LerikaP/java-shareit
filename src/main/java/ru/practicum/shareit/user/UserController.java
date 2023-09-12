package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserDto addUser(@Validated(Create.class) @RequestBody UserDto userDto) {
        return userService.addUser(userDto);
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@PathVariable long id, @Validated(Update.class) @RequestBody UserDto userDto) {
        return userService.updateUser(userDto, id);
    }

    @DeleteMapping("{id}")
    public void deleteUserById(@PathVariable long id) {
        userService.deleteUserById(id);
    }

}
