package ru.practicum.shareit.item;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.OwnerPermissionException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.CustomPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemServiceTest {
    private static ItemService itemService;
    private static ItemRepository itemRepository;
    private static UserRepository userRepository;
    private static CommentRepository commentRepository;
    private static ItemRequestRepository itemRequestRepository;
    private static BookingRepository bookingRepository;
    private static ItemDto itemDto;
    private static User user;
    private static Item item;
    private static Booking lastBooking;
    private static Booking nextBooking;
    private static Comment comment;
    private static ItemRequest itemRequest;
    private static ItemDtoRequest itemDtoRequest;

    @BeforeAll
    static void beforeAll() {
        LocalDateTime currentTime = LocalDateTime.now();
        user = new User(1L, "user", "user@mail.com");
        item = new Item(1L, "item", "description", Boolean.TRUE);
        lastBooking = new Booking(1L, currentTime.minusDays(1), currentTime.plusDays(1),
                item, user, BookingStatus.APPROVED);
        nextBooking = new Booking(2L, currentTime.plusDays(2), currentTime.plusDays(4),
                item, user, BookingStatus.APPROVED);
        comment = new Comment(1L, "comment", item, user, currentTime);
        itemRequest = new ItemRequest(1L, "description", user, currentTime);
        itemDto = new ItemDto(1L, "item", "description", Boolean.TRUE, 1L);
        itemDtoRequest = new ItemDtoRequest("item", "description", Boolean.TRUE, 1L);
    }

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository,
                commentRepository, itemRequestRepository);
    }

    @Test
    @Order(value = 1)
    void should_create_item() {
        item.setOwner(user);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDtoForTest = itemService.addItem(itemDtoRequest, user.getId());

        assertThat(itemDtoForTest)
                .hasFieldOrPropertyWithValue("id", itemDto.getId())
                .hasFieldOrPropertyWithValue("name", itemDto.getName())
                .hasFieldOrPropertyWithValue("description", itemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", itemDto.getAvailable());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @Order(value = 2)
    void should_update_item() {
        Item updateItem = new Item(1L, "new name", "description", Boolean.TRUE);
        ItemDto updateItemDto = new ItemDto(1L, "new name", "description", Boolean.TRUE, 1L);

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(updateItem);

        ItemDto itemDtoForTest = itemService.updateItem(updateItemDto, item.getId(), user.getId());

        assertThat(itemDtoForTest)
                .hasFieldOrPropertyWithValue("id", updateItemDto.getId())
                .hasFieldOrPropertyWithValue("name", updateItemDto.getName())
                .hasFieldOrPropertyWithValue("description", updateItemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", updateItemDto.getAvailable());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    @Order(value = 3)
    void should_find_all_items_by_owner_id() {
        final List<Item> items = new ArrayList<>(Collections.singleton(item));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByOwner_IdOrderById(anyLong(), any(CustomPageRequest.class)))
                .thenReturn(items);

        List<ItemDtoWithBooking> itemDtos = itemService.getAllItemsByUserId(user.getId(), 1, 10);

        assertThat(itemDtos)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", item.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name",item.getName());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", item.getDescription());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("available", item.getAvailable());
                });
    }

    @Test
    @Order(value = 4)
    void should_find_item_by_id() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByItem_IdAndStatus(anyLong(), any(BookingStatus.class)))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentRepository.findByItem_IdOrderByCreatedDesc(anyLong()))
                .thenReturn(Collections.singletonList(comment));


        ItemDtoWithBooking itemDtoForTest = itemService.getItemById(item.getId(), user.getId());

        assertThat(itemDtoForTest)
                .hasFieldOrPropertyWithValue("id", itemDtoForTest.getId())
                .hasFieldOrPropertyWithValue("name", itemDtoForTest.getName())
                .hasFieldOrPropertyWithValue("description", itemDtoForTest.getDescription())
                .hasFieldOrPropertyWithValue("available", itemDtoForTest.getAvailable())
                .hasFieldOrPropertyWithValue("lastBooking", itemDtoForTest.getLastBooking())
                .hasFieldOrPropertyWithValue("nextBooking", itemDtoForTest.getNextBooking())
                .hasFieldOrPropertyWithValue("comments", itemDtoForTest.getComments());

    }

    @Test
    @Order(value = 5)
    void should_search_items_by_text() {
        final List<Item> items = new ArrayList<>(Collections.singleton(item));

        when(itemRepository.search(anyString(), any(CustomPageRequest.class)))
                .thenReturn(items);

        List<ItemDto> itemDtos = itemService.searchItem("text", 1, 10);

        assertThat(itemDtos)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", item.getId());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name",item.getName());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", item.getDescription());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("available", item.getAvailable());
                });

    }

    @Test
    @Order(value = 6)
    void should_create_comment() {
        CommentRequestDto commentRequestDto = new CommentRequestDto("comment");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByBooker_IdAndItem_IdAndEndIsBeforeAndStatusOrderByStart(anyLong(), anyLong(),
                any(LocalDateTime.class), any(BookingStatus.class)))
                .thenReturn(List.of(lastBooking, nextBooking));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        CommentResponseDto commentDtoForTest = itemService.addComment(item.getId(), commentRequestDto, user.getId());

        assertThat(commentDtoForTest)
                .hasFieldOrPropertyWithValue("id", comment.getId())
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("authorName",comment.getAuthor().getName())
                .hasFieldOrPropertyWithValue("created", comment.getCreated());
    }

    @Test
    @Order(value = 7)
    void should_not_create_comment_with_no_bookings() {
        CommentRequestDto commentRequestDto = new CommentRequestDto("comment");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class)))
                .thenThrow(new BookingValidationException("Указаны не верные данные"));

        final BookingValidationException e = Assertions.assertThrows(BookingValidationException.class,
                () -> itemService.addComment(item.getId(), commentRequestDto, user.getId()));

        assertEquals(String.format("Пользователь с id %s не бронировал вещь %s", user.getId(), item.getName()),
                e.getMessage());
    }

    @Test
    @Order(value = 8)
    void should_not_update_item_by_not_owner() {
        User notOwnerUser = new User(2L, "user 2", "user@@mail.com");
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(notOwnerUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        final OwnerPermissionException e = Assertions.assertThrows(
                OwnerPermissionException.class, () -> itemService.updateItem(itemDto, item.getId(),
                        notOwnerUser.getId()));

        assertEquals(String.format("Пользователь %s не является владельцем вещи",
                notOwnerUser.getName()), e.getMessage());
    }


    @Test
    @Order(value = 9)
    void should_return_empty_list_with_empty_search_text() {
        final List<Item> items = Collections.emptyList();

        when(itemRepository.search(anyString(), any(CustomPageRequest.class)))
                .thenReturn(items);

        List<ItemDto> itemDtos = itemService.searchItem("", 1, 10);

        assertThat(itemDtos).isEmpty();
    }

}
