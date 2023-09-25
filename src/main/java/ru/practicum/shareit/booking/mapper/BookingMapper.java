package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDtoItem;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {

    public static Booking toBooking(BookingDtoRequest bookingDtoRequest, User user, Item item) {
        Booking booking = new Booking();
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    public static BookingDtoResponse toBookingDtoResponse(Booking booking) {
        BookingDtoResponse bookingDtoResponse = new BookingDtoResponse();
        bookingDtoResponse.setId(booking.getId());
        bookingDtoResponse.setStart(booking.getStart());
        bookingDtoResponse.setEnd(booking.getEnd());
        bookingDtoResponse.setStatus(booking.getStatus());
        bookingDtoResponse.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDtoResponse.setBooker(UserMapper.toUserDto(booking.getBooker()));
        return bookingDtoResponse;
    }

    public static BookingDtoItem toBookingDtoItem(Booking booking) {
        BookingDtoItem bookingDtoItem = new BookingDtoItem();
        bookingDtoItem.setId(booking.getId());
        bookingDtoItem.setStart(booking.getStart());
        bookingDtoItem.setEnd(booking.getEnd());
        bookingDtoItem.setBookerId(booking.getBooker().getId());
        bookingDtoItem.setStatus(booking.getStatus());
        return bookingDtoItem;
    }
}
