package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {

    BookingDtoResponse addBooking(long userId, BookingDtoRequest bookingDtoRequest);

    BookingDtoResponse changeBookingStatus(long id, long userId, boolean approved);

    BookingDtoResponse getBookingById(long id, long userId);

    List<BookingDtoResponse> getAllBookingsByUserId(long userId, String status, int from, int size);

    List<BookingDtoResponse> getAllBookingsByItemOwnerId(long userId, String status, int from, int size);

}
