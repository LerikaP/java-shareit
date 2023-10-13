package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemServiceImplTest {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final EntityManager em;
    private static ItemDto itemDto;
    private static UserDto userDto;
    private static ItemRequestDto request;
    private static ItemRequestResponseDto requestResponseDto;
    private static ItemDtoRequest itemDtoRequest;
    private static UserDtoRequest userDtoRequest;

    @BeforeAll
    static void setUp() {
        request = new ItemRequestDto("text");
        itemDto = new ItemDto(1L, "item", "description", Boolean.TRUE, 1L);
        userDto = new UserDto(1L, "user 1","user1@mail.com");
        itemDtoRequest = new ItemDtoRequest("item", "description", Boolean.TRUE);
        userDtoRequest = new UserDtoRequest("user 1","user1@mail.com");
    }

    @Test
    @Order(value = 1)
    void should_create_item() {
        userDto = userService.addUser(userDtoRequest);
        itemRequestService.addItemRequest(request, userDto.getId());
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(item.getId(), equalTo(itemDto.getId()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    @Order(value = 2)
    void should_update_item() {
        userDto = userService.addUser(userDtoRequest);
        requestResponseDto = itemRequestService.addItemRequest(request, userDto.getId());
        itemDto.setRequestId(requestResponseDto.getId());
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());

        ItemDto updateItemDto = new ItemDto(2L, "new item", "description", Boolean.TRUE, 1L);

        itemService.updateItem(updateItemDto, itemDto.getId(), userDto.getId());

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item item = query.setParameter("id", itemDto.getId()).getSingleResult();

        assertThat(item.getName(), equalTo(updateItemDto.getName()));
    }

    @Test
    @Order(value = 3)
    void should_get_item_by_id() {
        userDto = userService.addUser(userDtoRequest);
        requestResponseDto = itemRequestService.addItemRequest(request, userDto.getId());
        itemDto.setRequestId(requestResponseDto.getId());
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());

        ItemDtoWithBooking item = itemService.getItemById(itemDto.getId(), userDto.getId());

        assertThat(item.getId(), equalTo(itemDto.getId()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    @Order(value = 4)
    void should_return_all_items_by_owner_id() {
        userDto = userService.addUser(userDtoRequest);
        requestResponseDto = itemRequestService.addItemRequest(request, userDto.getId());
        itemDto.setRequestId(requestResponseDto.getId());
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());

        ItemDto newItemDto = itemService.addItem(new ItemDtoRequest("new item", "description",
                Boolean.TRUE, requestResponseDto.getId()), userDto.getId());


        List<ItemDtoWithBooking> itemDtos = itemService.getAllItemsByUserId(userDto.getId(), 0, 10);

        Assertions.assertThat(itemDtos)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", itemDto.getId());
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", itemDto.getName());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", newItemDto.getId());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", newItemDto.getName());
                });
    }

    @Test
    @Order(value = 5)
    void should_delete_item() {
        userDto = userService.addUser(userDtoRequest);
        requestResponseDto = itemRequestService.addItemRequest(request, userDto.getId());
        itemDto.setRequestId(requestResponseDto.getId());
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());

        assertThat(itemService.getAllItemsByUserId(userDto.getId(), 0, 10).size(), equalTo(1));

        itemService.deleteItemById(itemDto.getId());

        assertThat(itemService.getAllItemsByUserId(userDto.getId(), 0, 10).size(), equalTo(0));
    }
}
