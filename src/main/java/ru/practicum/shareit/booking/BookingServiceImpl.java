package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoResponse addBooking(long userId, BookingDtoRequest bookingDtoRequest) {
        User user = getUserForBooking(userId);
        long itemId = bookingDtoRequest.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->  new NotFoundException(String.format("Вещь с id %s не найдена", itemId)));
        if (!item.getAvailable()) {
            throw new BookingValidationException("Вещь недоступна для бронирования");
        }
        if (userId == item.getOwner().getId()) {
            throw new NotFoundException("Владелец не может забронировать свою вещь");
        }
        Booking booking = BookingMapper.toBooking(bookingDtoRequest, user, item);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse changeBookingStatus(long id, long userId, boolean approved) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id %s не найдено", id)));
        User user = getUserForBooking(userId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new OwnerPermissionException(
                    String.format("Пользователь %s не является владельцем вещи", user.getName()));
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new BookingValidationException("Бронирование уже подтверждено");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse getBookingById(long id, long userId) {
        User user = getUserForBooking(userId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));
        if (!(booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId)) {
            throw new NotFoundException(String.format(
                    "Пользователь %s не является владельцем вещи либо автором бронирования", user.getName()));
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByUserId(long userId, String state) {
        getUserForBooking(userId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (RuntimeException e) {
            throw new BookingWrongStatusException(String.format("Unknown state: %s", state));
        }
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByBooker_IdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatusOrderByStartDesc(userId,
                        BookingStatus.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByItemOwnerId(long ownerId, String state) {
        getUserForBooking(ownerId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (RuntimeException e) {
            throw new BookingWrongStatusException(String.format("Unknown state: %s", state));
        }
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBeforeOrderByStartDesc(ownerId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfterOrderByStartDesc(ownerId,
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId,
                        BookingStatus.REJECTED);
                break;
            default:
                bookings = Collections.emptyList();
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    private User getUserForBooking(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %s не найден", id)));
    }
}
