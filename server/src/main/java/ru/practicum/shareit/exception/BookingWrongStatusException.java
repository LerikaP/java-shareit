package ru.practicum.shareit.exception;

public class BookingWrongStatusException extends RuntimeException {

    public BookingWrongStatusException(String message) {
        super(message);
    }
}
