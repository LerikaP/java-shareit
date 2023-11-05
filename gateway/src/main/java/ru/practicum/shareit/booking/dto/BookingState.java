package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum BookingState {
	// Все
	ALL,
	// Текущие
	CURRENT,
	// Будущие
	FUTURE,
	// Завершенные
	PAST,
	// Отклоненные
	REJECTED,
	// Ожидающие подтверждения
	WAITING;

	public static Optional<BookingState> from(String stringState) {
		for (BookingState state : values()) {
			if (state.name().equals(stringState.toUpperCase())) {
				return Optional.of(state);
			}
		}
		return Optional.empty();
	}
}
