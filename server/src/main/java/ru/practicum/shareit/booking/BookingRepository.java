package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface  BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker_Id(long userId, PageRequest pageRequest);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(long userId,
                                                               LocalDateTime start,
                                                               LocalDateTime end, PageRequest pageRequest);

    List<Booking> findByBooker_IdAndEndIsBefore(long userId, LocalDateTime end,
                                                PageRequest pageRequest);

    List<Booking> findByBooker_IdAndStartIsAfter(long userId, LocalDateTime start,
                                                 PageRequest pageRequest);

    List<Booking> findByBooker_IdAndStatus(long userId, BookingStatus status, PageRequest pageRequest);

    List<Booking> findByItem_Owner_Id(long ownerId, PageRequest pageRequest);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(
            long ownerId,
            LocalDateTime start,
            LocalDateTime end,
            PageRequest pageRequest);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(
            long ownerId,
            LocalDateTime end,
            PageRequest pageRequest);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(
            long ownerId,
            LocalDateTime start,
            PageRequest pageRequest);

    List<Booking> findByItem_Owner_IdAndStatus(
            long ownerId,
            BookingStatus status,
            PageRequest pageRequest);

    List<Booking> findByBooker_IdAndItem_IdAndEndIsBeforeAndStatusOrderByStart(
            long userId,
            long itemId,
            LocalDateTime end,
            BookingStatus status);

    List<Booking> findByItem_IdAndStatus(long itemId, BookingStatus status);

}
