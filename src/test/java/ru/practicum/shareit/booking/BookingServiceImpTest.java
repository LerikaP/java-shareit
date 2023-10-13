package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoRequest;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookingServiceImpTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager em;
    private static UserDto userDto;
    private static UserDto userDtoSecond;
    private static ItemDto itemDto;
    private static BookingDtoRequest bookingDtoRequest;
    private static UserDtoRequest userDtoRequest;
    private static ItemDtoRequest itemDtoRequest;
    private static UserDtoRequest userDtoRequestSecond;

    @BeforeAll
    static void beforeAll() {
        userDto = new UserDto(1L, "user 1", "user1@email.com");
        userDtoSecond = new UserDto(2L, "user 2", "user2@email.com");
        itemDto = new ItemDto(1L, "item", "description", Boolean.TRUE);
        bookingDtoRequest = new BookingDtoRequest(itemDto.getId(), LocalDateTime.now().minusDays(10),
                LocalDateTime.now().minusDays(3));
        userDtoRequest = new UserDtoRequest("user 1", "user1@email.com");
        itemDtoRequest = new ItemDtoRequest("item", "description", Boolean.TRUE);
        userDtoRequestSecond = new UserDtoRequest("user 2", "user2@email.com");
    }

    @Test
    @Order(value = 1)
    void should_create_booking() {
        userDto = userService.addUser(userDtoRequest);
        userDtoSecond = userService.addUser(userDtoRequestSecond);
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());
        BookingDtoResponse bookingDtoResponse = bookingService.addBooking(userDtoSecond.getId(), bookingDtoRequest);

        TypedQuery<Booking> query = em.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDtoResponse.getId()).getSingleResult();

        assertThat(booking.getItem().getId(), equalTo(itemDto.getId()));
        assertThat(booking.getBooker().getId(), equalTo(userDtoSecond.getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    @Order(value = 2)
    void should_change_booking_status() {
        userDto = userService.addUser(userDtoRequest);
        userDtoSecond = userService.addUser(userDtoRequestSecond);
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());
        bookingDtoRequest.setItemId(itemDto.getId());
        BookingDtoResponse bookingDtoResponse = bookingService.addBooking(userDtoSecond.getId(), bookingDtoRequest);
        bookingService.changeBookingStatus(bookingDtoResponse.getId(), userDto.getId(), Boolean.TRUE);

        TypedQuery<Booking> query = em.createQuery("Select i from Booking i where i.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingDtoResponse.getId()).getSingleResult();

        assertThat(booking.getItem().getId(), equalTo(itemDto.getId()));
        assertThat(booking.getBooker().getId(), equalTo(userDtoSecond.getId()));
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    @Order(value = 3)
    void should_get_booking_by_id() {
        userDto = userService.addUser(userDtoRequest);
        userDtoSecond = userService.addUser(userDtoRequestSecond);
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());
        bookingDtoRequest.setItemId(itemDto.getId());
        BookingDtoResponse bookingDtoResponse = bookingService.addBooking(userDtoSecond.getId(), bookingDtoRequest);

        BookingDtoResponse bookingDtoForTest = bookingService.getBookingById(bookingDtoResponse.getId(),
                userDto.getId());

        assertThat(bookingDtoForTest.getItem().getId(), equalTo(itemDto.getId()));
        assertThat(bookingDtoForTest.getBooker().getId(), equalTo(userDtoSecond.getId()));
        assertThat(bookingDtoForTest.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    @Order(value = 4)
    void should_get_all_bookings_by_user_id() {
        userDto = userService.addUser(userDtoRequest);
        userDtoSecond = userService.addUser(userDtoRequestSecond);
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());
        bookingDtoRequest.setItemId(itemDto.getId());
        BookingDtoResponse bookingDtoResponse = bookingService.addBooking(userDtoSecond.getId(), bookingDtoRequest);

        BookingDtoResponse bookingDtoResponseSecond = bookingService.addBooking(userDtoSecond.getId(),
                new BookingDtoRequest(itemDto.getId(),
                        LocalDateTime.now().minusDays(2),LocalDateTime.now().minusDays(1)));

        List<BookingDtoResponse> bookingsDtos = bookingService.getAllBookingsByUserId(userDtoSecond.getId(),
                "WAITING", 0, 10);

        Assertions.assertThat(bookingsDtos)
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id",
                            bookingDtoResponseSecond.getId());
                    Assertions.assertThat(list.get(1)).hasFieldOrPropertyWithValue("id",
                            bookingDtoResponse.getId());
                });
    }

    @Test
    @Order(value = 5)
    void should_get_all_bookings_by_item_owner_id() {
        userDto = userService.addUser(userDtoRequest);
        userDtoSecond = userService.addUser(userDtoRequestSecond);
        itemDto = itemService.addItem(itemDtoRequest, userDto.getId());
        bookingDtoRequest.setItemId(itemDto.getId());
        BookingDtoResponse bookingDtoResponse = bookingService.addBooking(userDtoSecond.getId(), bookingDtoRequest);

        List<BookingDtoResponse> bookingsDtos = bookingService.getAllBookingsByItemOwnerId(userDto.getId(),
                "WAITING", 0, 10);

        Assertions.assertThat(bookingsDtos)
                .isNotEmpty()
                .hasSize(1)
                .satisfies(list -> Assertions.assertThat(list.get(0)).hasFieldOrPropertyWithValue("id",
                        bookingDtoResponse.getId()));
    }

}
