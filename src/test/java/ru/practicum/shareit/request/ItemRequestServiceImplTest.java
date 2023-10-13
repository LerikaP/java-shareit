package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemRequestServiceImplTest {
    private final ItemRequestService requestService;
    private final UserService userService;
    private final EntityManager em;
    private static UserDto userDto;
    private static UserDto secondUserDto;
    private static ItemRequestDto requestDto;
    private static UserDtoRequest userDtoRequest;
    private static UserDtoRequest secondUserDtoRequest;
    private static ItemRequestResponseDto requestResponseDto;

    @BeforeAll
    static void setUp() {
        requestDto = new ItemRequestDto("request description");
        userDtoRequest = new UserDtoRequest("user 1", "user1@email.com");
        secondUserDtoRequest = new UserDtoRequest("user 2", "user2@email.com");
    }

    @Test
    @Order(value = 1)
    void should_create_user() {
        userDto = userService.addUser(userDtoRequest);
        requestResponseDto = requestService.addItemRequest(requestDto, userDto.getId());

        TypedQuery<ItemRequest> query = em.createQuery("Select i from ItemRequest i where i.id = :id",
                ItemRequest.class);
        ItemRequest request = query.setParameter("id", requestResponseDto.getId()).getSingleResult();

        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
    }

    @Test
    @Order(value = 2)
    void should_get_item_request_by_id() {
        userDto = userService.addUser(userDtoRequest);
        requestResponseDto = requestService.addItemRequest(requestDto, userDto.getId());

        ItemRequestResponseDto request = requestService.getItemRequestById(requestResponseDto.getId(), userDto.getId());

        assertThat(request.getDescription(), equalTo(requestDto.getDescription()));
        assertThat(Collections.emptyList(), equalTo(request.getItems()));
    }

    @Test
    @Order(value = 3)
    void should_get_all_requests_by_user_id() {
        userDto = userService.addUser(userDtoRequest);
        secondUserDto = userService.addUser(secondUserDtoRequest);
        requestService.addItemRequest(requestDto, userDto.getId());
        requestService.addItemRequest(requestDto, secondUserDto.getId());

        List<ItemRequestResponseDto> responseDtos = requestService.getAllRequestsByUserId(userDto.getId());

        Assertions.assertThat(responseDtos)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("description",
                        requestDto.getDescription()));
    }

    @Test
    @Order(value = 4)
    void should_get_all_requests() {
        userDto = userService.addUser(userDtoRequest);
        secondUserDto = userService.addUser(secondUserDtoRequest);
        ItemRequestDto secondRequestDto = new ItemRequestDto("description 2");
        requestService.addItemRequest(requestDto, userDto.getId());
        requestService.addItemRequest(secondRequestDto, userDto.getId());

        List<ItemRequestResponseDto> responseDtos =
                requestService.getAllRequests(secondUserDto.getId(), 0, 10);

        Assertions.assertThat(responseDtos)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("description",
                            secondRequestDto.getDescription());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("description",
                            requestDto.getDescription());
                });
    }
}
