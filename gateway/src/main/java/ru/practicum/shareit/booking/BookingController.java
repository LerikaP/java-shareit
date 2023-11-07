package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.BookingWrongStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;
	private static final String USER_ID_HEADER = "X-Sharer-User-Id";

	@GetMapping
	public ResponseEntity<Object> getBookingsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
													  @RequestParam(name = "state", defaultValue = "all")
													  String stateParam,
													  @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
														  Integer from,
													  @Positive @RequestParam(name = "size", defaultValue = "10")
														  Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new BookingWrongStatusException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsByUserId(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestBody @Valid BookItemRequestDto requestDto,
										   @RequestHeader(USER_ID_HEADER) long userId) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(USER_ID_HEADER) long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approve(@PathVariable long bookingId,
										  @RequestParam boolean approved,
										  @RequestHeader(USER_ID_HEADER) long userId) {
		log.info("Update booking {}, userId={}", bookingId, userId);
		return bookingClient.approveBooking(bookingId, userId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsByItemOwnerId(@RequestHeader(USER_ID_HEADER) long userId,
														   @RequestParam(name = "state", defaultValue = "all")
														   String stateParam,
														   @PositiveOrZero @RequestParam(name = "from",
																   defaultValue = "0") Integer from,
														   @Positive @RequestParam(name = "size", defaultValue = "10")
															   Integer size) {
			BookingState state = BookingState.from(stateParam)
					.orElseThrow(() -> new BookingWrongStatusException("Unknown state: " + stateParam));
			log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
			return bookingClient.getBookingsByItemOwnerId(userId, state, from, size);
		}

}
