package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper mapper;
    private static UserDtoRequest userDtoRequest;
    private static UserDto userDto;

    @BeforeAll
    static void setUp() {
        userDtoRequest = new UserDtoRequest("user 1", "user1@email.com");
        userDto = new UserDto(1L, "user 1", "user1@email.com");
    }

    @Test
    @SneakyThrows
    void should_create_user() {
        when(userService.addUser(any(UserDtoRequest.class)))
                .thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
        verify(userService, times(1)).addUser(any(UserDtoRequest.class));
    }

    @Test
    @SneakyThrows
    void should_update_user() {
        when(userService.updateUser(any(UserDto.class), anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
        verify(userService, times(1)).updateUser(any(UserDto.class), anyLong());
    }

    @Test
    @SneakyThrows
    void should_delete_user_by_id() {
        doNothing().when(userService).deleteUserById(anyLong());

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(anyLong());
    }

    @Test
    @SneakyThrows
    void should_get_user_by_id() {
        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDto.getId()))
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));
        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    @SneakyThrows
    void should_get_all_users() {
        List<UserDto> expectedResult = Collections.singletonList(userDto);
        when(userService.getAllUsers())
                .thenReturn(expectedResult);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(expectedResult)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}
