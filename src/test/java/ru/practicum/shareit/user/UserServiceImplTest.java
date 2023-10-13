package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserServiceImplTest {
    private final UserService userService;
    private final EntityManager em;
    private static UserDto userDto;
    private static UserDtoRequest userDtoRequest;

    @BeforeAll
    static void setUp() {
        userDto = new UserDto(1L, "user 1","user1@mail.com");
        userDtoRequest = new UserDtoRequest("user 1", "user1@mail.com");
    }

    @Test
    @Order(value = 1)
    void should_create_user() {
        userDto = userService.addUser(userDtoRequest);

        TypedQuery<User> query = em.createQuery("Select i from User i where i.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto.getId()));
        assertThat(user.getName(), equalTo(userDtoRequest.getName()));
        assertThat(user.getEmail(), equalTo(userDtoRequest.getEmail()));
    }

    @Test
    @Order(value = 2)
    void should_update_user() {
        userDto = userService.addUser(userDtoRequest);

        UserDto userDtoForUpdate = new UserDto(2L, "new name", "user1@mail.com");

        userService.updateUser(userDtoForUpdate, userDto.getId());

        TypedQuery<User> query = em.createQuery("Select i from User i where i.id = :id", User.class);
        User user = query.setParameter("id", userDto.getId()).getSingleResult();

        assertThat(user.getName(), equalTo(userDtoForUpdate.getName()));

    }

    @Test
    @Order(value = 3)
    void should_get_user_by_id() {
        userDto = userService.addUser(userDtoRequest);

        UserDto user = userService.getUserById(userDto.getId());

        assertThat(userDto.getId(), equalTo(user.getId()));
        assertThat(userDto.getName(), equalTo(user.getName()));
        assertThat(userDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    @Order(value = 4)
    void should_return_all_users() {
        userDto = userService.addUser(userDtoRequest);
        UserDto newUser = userService.addUser(new UserDtoRequest("user 2", "user2@mail.com"));

        List<UserDto> userDtos = userService.getAllUsers();

        assertThat(userDtos)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", userDto.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("email", userDto.getEmail());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", newUser.getId());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("email", newUser.getEmail());
                });

    }

    @Test
    @Order(value = 5)
    void should_delete_user() {
        userDto = userService.addUser(userDtoRequest);

        assertThat(userService.getAllUsers().size(), equalTo(1));

        userService.deleteUserById(userDto.getId());

        assertThat(userService.getAllUsers().size(), equalTo(0));
    }

}
