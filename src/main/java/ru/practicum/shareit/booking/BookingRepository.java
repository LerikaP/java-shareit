package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface  BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_IdOrderByStartDesc(long userId);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId,
                                                                     LocalDateTime start,
                                                                     LocalDateTime end);

    List<Booking> findByBooker_IdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime end);

    List<Booking> findByBooker_IdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    List<Booking> findByBooker_IdAndStatusOrderByStartDesc(long userId, BookingStatus status);

    List<Booking> findByItem_Owner_IdOrderByStartDesc(long ownerId);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            long ownerId,
            LocalDateTime start,
            LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(
            long ownerId,
            LocalDateTime end);

    List<Booking> findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(
            long ownerId,
            LocalDateTime start);

    List<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(
            long ownerId,
            BookingStatus status);

    List<Booking> findByBooker_IdAndItem_IdAndEndIsBeforeAndStatusOrderByStart(
            long userId,
            long itemId,
            LocalDateTime end,
            BookingStatus status);

    List<Booking> findByItem_IdAndStatus(long itemId, BookingStatus status);

}
