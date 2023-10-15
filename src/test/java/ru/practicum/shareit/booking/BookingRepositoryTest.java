package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.CustomPageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BookingRepository bookingRepository;
    private User secondUser;
    private Item item;
    private CustomPageRequest pageRequest;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        pageRequest = new CustomPageRequest(0, 10, Sort.unsorted());
        now = LocalDateTime.now();

        User user = userRepository.save(new User(1L, "user 1", "user1@email.com"));
        secondUser = userRepository.save(new User(2L, "user 2", "user2@email.com"));
        item = itemRepository.save(new Item(1L, "item 1", "description 1", true));
        item.setOwner(user);

        Booking booking = bookingRepository.save(new Booking(1L, now.minusDays(2), now.minusDays(1), item, secondUser,
                BookingStatus.APPROVED));
        Booking secondBooking = bookingRepository.save(new Booking(2L, now.plusDays(2), now.plusDays(3), item, secondUser,
                BookingStatus.WAITING));
    }

    @Test
    void should_find_bookings_by_booker_id() {
        final List<Booking> bookings = bookingRepository.findByBooker_IdOrderByStartDesc(secondUser.getId(),
                pageRequest);

        assertEquals(2, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
        assertEquals(secondUser, bookings.get(1).getBooker());
    }

    @Test
    void should_find_bookings_by_booker_id_and_current_state() {
        final List<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                secondUser.getId(), now, now, pageRequest);

        assertEquals(0, bookings.size());
    }

    @Test
    void should_find_bookings_by_booker_id_and_past_state() {
        final List<Booking> bookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(
                secondUser.getId(), now, pageRequest);

        assertEquals(1, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
    }

    @Test
    void should_find_bookings_by_booker_id_and_future_state() {
        final List<Booking> bookings = bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(
                secondUser.getId(),now, pageRequest);

        assertEquals(1, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
    }

    @Test
    void should_find_bookings_by_booker_id_and_waiting_status() {
        final List<Booking> bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(
                secondUser.getId(), BookingStatus.WAITING, pageRequest);

        assertEquals(1, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void should_find_bookings_by_item_owner_id() {
        final List<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(item.getOwner().getId(),
                pageRequest);

        assertEquals(2, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
        assertEquals(secondUser, bookings.get(1).getBooker());
    }

    @Test
    void should_find_bookings_by_item_owner_id_and_current_state() {
        final List<Booking> bookings = bookingRepository
                .findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(item.getOwner().getId(), now, now,
                        pageRequest);

        assertEquals(0, bookings.size());
    }

    @Test
    void should_find_bookings_by_by_item_owner_id_and_past_state() {
        final List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(
                item.getOwner().getId(), now, pageRequest);

        assertEquals(1, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
    }

    @Test
    void should_find_bookings_by_item_owner_id_and_future_state() {
        final List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(
                item.getOwner().getId(),now, pageRequest);

        assertEquals(1, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
    }

    @Test
    void should_find_bookings_by_item_owner_id_and_waiting_status() {
        final List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(
                item.getOwner().getId(), BookingStatus.WAITING, pageRequest);

        assertEquals(1, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
        assertEquals(BookingStatus.WAITING, bookings.get(0).getStatus());
    }

    @Test
    void should_find_bookings_by_booker_id_and_item_id_and_past_state_and_status() {
        final List<Booking> bookings = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBeforeAndStatusOrderByStart(
                secondUser.getId(), item.getId(), now, BookingStatus.APPROVED);

        assertEquals(1, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());
    }

    @Test
    void should_find_by_item_id_and_status() {
        final List<Booking> bookings = bookingRepository.findByItem_IdAndStatus(item.getId(), BookingStatus.APPROVED);

        assertEquals(1, bookings.size());
        assertEquals(secondUser, bookings.get(0).getBooker());
        assertEquals(BookingStatus.APPROVED, bookings.get(0).getStatus());
    }


}
