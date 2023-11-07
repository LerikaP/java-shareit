package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import ru.practicum.shareit.utils.CustomPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
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

    @Transactional
    @Override
    public BookingDtoResponse changeBookingStatus(long id, long userId, boolean approved) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с id %s не найдено", id)));
        User user = getUserForBooking(userId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new OwnerPermissionException(
                    String.format("Пользователь c id %s не является владельцем вещи", user.getId()));
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
                    "Пользователь c id %s не является владельцем вещи либо автором бронирования", user.getId()));
        }
        return BookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByUserId(long userId, String state, int from, int size) {
        getUserForBooking(userId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (RuntimeException e) {
            throw new BookingWrongStatusException(String.format("Unknown state: %s", state));
        }
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByBooker_Id(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId,
                        BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId,
                        BookingStatus.REJECTED, pageRequest);
                break;
            default:
                bookings = bookingRepository.findByBooker_Id(userId, pageRequest);
        }
        return bookings.stream()
                .map(BookingMapper::toBookingDtoResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsByItemOwnerId(long ownerId, String state, int from, int size) {
        getUserForBooking(ownerId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state.toUpperCase());
        } catch (RuntimeException e) {
            throw new BookingWrongStatusException(String.format("Unknown state: %s", state));
        }
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.by(Sort.Direction.DESC, "start"));
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findByItem_Owner_Id(ownerId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(ownerId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndIsBefore(ownerId,
                        LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartIsAfter(ownerId,
                        LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId,
                        BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(ownerId,
                        BookingStatus.REJECTED, pageRequest);
                break;
            default:
                bookings = bookingRepository.findByItem_Owner_Id(ownerId, pageRequest);
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
