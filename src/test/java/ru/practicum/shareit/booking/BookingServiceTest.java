package ru.practicum.shareit.booking;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.BookingWrongStatusException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerPermissionException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class BookingServiceTest {
    private static BookingService bookingService;
    private static BookingRepository bookingRepository;
    private static ItemRepository itemRepository;
    private static UserRepository userRepository;
    private static User user;
    private static User secondUser;
    private static Item item;
    private static Booking lastBooking;
    private static Booking nextBooking;
    private static BookingDtoRequest bookingDtoRequest;

    @BeforeAll
    static void beforeAll() {
        user = new User(1L, "user", "user@mail.com");
        secondUser = new User(2L, "user 2", "user2@mail.com");
        item = new Item(1L, "item", "description", Boolean.TRUE);
        lastBooking = new Booking(1L, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(2),
                item, user, BookingStatus.APPROVED);
        nextBooking = new Booking(2L, LocalDateTime.now().plusDays(2),LocalDateTime.now().plusDays(4),
                item, user, BookingStatus.WAITING);
        bookingDtoRequest = new BookingDtoRequest(item.getId(), LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(4));
        item.setOwner(user);
    }

    @BeforeEach
    void setUp() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository);
        item.setAvailable(Boolean.TRUE);
    }

    @Test
    void should_not_create_booking_with_not_found_user() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(nextBooking);

        final NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(100L, bookingDtoRequest));

        assertEquals(String.format("Пользователь c id %s не найден", 100L), e.getMessage());
    }

    @Test
    void should_not_create_booking_with_owner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(nextBooking);

        final NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.addBooking(user.getId(), bookingDtoRequest));

        assertEquals("Владелец не может забронировать свою вещь", e.getMessage());
    }

    @Test
    void should_not_create_booking_with_not_available_item() {
        item.setAvailable(Boolean.FALSE);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(nextBooking);

        final BookingValidationException e = Assertions.assertThrows(BookingValidationException.class,
                () -> bookingService.addBooking(secondUser.getId(), bookingDtoRequest));

        assertEquals("Вещь недоступна для бронирования", e.getMessage());
    }

    @Test
    void should_not_change_status_by_not_owner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(nextBooking));

        final OwnerPermissionException e = Assertions.assertThrows(OwnerPermissionException.class,
                () -> bookingService.changeBookingStatus(nextBooking.getId(), secondUser.getId(), Boolean.TRUE));

        assertEquals(String.format("Пользователь c id %s не является владельцем вещи", secondUser.getId()),
                e.getMessage());
    }

    @Test
    void should_not_change_status_with_approved_status() {
        nextBooking.setStatus(BookingStatus.APPROVED);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(nextBooking));

        final BookingValidationException e = Assertions.assertThrows(BookingValidationException.class,
                () -> bookingService.changeBookingStatus(nextBooking.getId(), user.getId(), Boolean.TRUE));

        assertEquals("Бронирование уже подтверждено", e.getMessage());
    }

    @Test
    void should_not_get_booking_by_wrong_user_id() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondUser));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(nextBooking));

        final NotFoundException e = Assertions.assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(nextBooking.getId(), secondUser.getId()));

        assertEquals(String.format(
                "Пользователь c id %s не является владельцем вещи либо автором бронирования", secondUser.getId()),
                e.getMessage());
    }

    @Test
    void should_change_status_to_rejected() {
        nextBooking.setStatus(BookingStatus.REJECTED);
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(nextBooking));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(nextBooking);

        BookingDtoResponse bookingDtoForTest = bookingService.changeBookingStatus(nextBooking.getId(), user.getId(),
                Boolean.FALSE);

        assertThat(bookingDtoForTest)
                .hasFieldOrPropertyWithValue("id", bookingDtoForTest.getId())
                .hasFieldOrPropertyWithValue("start",bookingDtoForTest.getStart())
                .hasFieldOrPropertyWithValue("end", bookingDtoForTest.getEnd())
                .hasFieldOrPropertyWithValue("status", bookingDtoForTest.getStatus());
    }

    @Test
    void should_find_all_bookings_by_user_id() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondUser));
        when(bookingRepository.findByBooker_IdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking, nextBooking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(
                secondUser.getId(), "ALL", 0, 10);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", lastBooking.getId());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id",nextBooking.getId());
                });
    }

    @Test
    void should_find_all_bookings_by_user_id_and_current_state() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondUser));
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(
                secondUser.getId(), "CURRENT", 0, 10);

        assertThat(bookings).isEmpty();
    }

    @Test
    void should_find_all_bookings_by_user_id_and_past_state() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondUser));
        when(bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(Collections.singletonList(lastBooking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(
                secondUser.getId(), "PAST", 0, 10);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list ->
                        assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", lastBooking.getId()));
    }

    @Test
    void should_find_all_bookings_by_user_id_and_future_state() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondUser));
        when(bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(
                secondUser.getId(), "FUTURE", 0, 10);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list ->
                        assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", nextBooking.getId()));
    }

    @Test
    void should_find_all_bookings_by_user_id_and_rejected_status() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondUser));
        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByUserId(
                secondUser.getId(), "REJECTED", 0, 10);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list ->
                        assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", nextBooking.getId()));
    }

    @Test
    void should_not_return_bookings_by_user_id_and_wrong_state() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(secondUser));
        when(bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        final BookingWrongStatusException e = Assertions.assertThrows(
                BookingWrongStatusException.class,
                () -> bookingService.getAllBookingsByUserId(secondUser.getId(), "PRESENT", 0, 10));

        assertEquals(String.format("Unknown state: %s", "PRESENT"), e.getMessage());
    }

    @Test
    void should_find_all_bookings_by_item_owner_id() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdOrderByStartDesc(anyLong(), any(PageRequest.class)))
                .thenReturn(List.of(lastBooking, nextBooking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByItemOwnerId(
                user.getId(), "ALL", 0, 10);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", lastBooking.getId());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id",nextBooking.getId());
                });
    }

    @Test
    void should_find_all_bookings_by_item_owner_id_and_current_state() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(),
                any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByItemOwnerId(
                user.getId(), "CURRENT", 0, 10);

        assertThat(bookings)
                .isEmpty();
    }

    @Test
    void should_find_all_bookings_by_item_owner_id_and_past_state() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(Collections.singletonList(lastBooking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByItemOwnerId(
                user.getId(), "PAST", 0, 10);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list ->
                        assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", lastBooking.getId()));
    }

    @Test
    void should_find_all_bookings_by_item_owner_id_and_future_state() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(anyLong(), any(LocalDateTime.class),
                any(PageRequest.class)))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByItemOwnerId(
                user.getId(), "FUTURE", 0, 10);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list ->
                        assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", nextBooking.getId()));
    }

    @Test
    void should_find_all_bookings_by_item_owner_id_and_rejected_status() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(Collections.singletonList(nextBooking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsByItemOwnerId(
                user.getId(), "REJECTED", 0, 10);

        assertThat(bookings)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list ->
                        assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", nextBooking.getId()));
    }

    @Test
    void should_not_return_bookings_by_item_owner_id_and_wrong_state() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), any(BookingStatus.class),
                any(PageRequest.class)))
                .thenReturn(Collections.emptyList());

        final BookingWrongStatusException e = Assertions.assertThrows(
                BookingWrongStatusException.class,
                () -> bookingService.getAllBookingsByItemOwnerId(user.getId(), "PRESENT", 0, 10));

        assertEquals(String.format("Unknown state: %s", "PRESENT"), e.getMessage());
    }
}
