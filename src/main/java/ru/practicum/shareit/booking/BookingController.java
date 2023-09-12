package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                           @RequestParam(value = "state", required = false,
                                                                   defaultValue = "ALL") String status) {
        return bookingService.getAllBookingsByUserId(userId, status);
    }

    @GetMapping("/{id}")
    public BookingDtoResponse getBookingById(@PathVariable long id, @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.getBookingById(id, userId);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsByItemOwnerId(@RequestHeader(USER_ID_HEADER) long userId,
                                                                @RequestParam(value = "state", required = false,
                                                                        defaultValue = "ALL") String status) {
        return bookingService.getAllBookingsByItemOwnerId(userId, status);
    }

    @PostMapping
    public BookingDtoResponse addBooking(@Valid @RequestBody BookingDtoRequest bookingDtoRequest,
                                         @RequestHeader(USER_ID_HEADER) long userId) {
        return bookingService.addBooking(userId, bookingDtoRequest);
    }

    @PatchMapping("/{id}")
    BookingDtoResponse changeBookingStatus(@PathVariable long id, @RequestHeader(USER_ID_HEADER) long userId,
                                           @RequestParam boolean approved) {
        return bookingService.changeBookingStatus(id, userId, approved);
    }
}
