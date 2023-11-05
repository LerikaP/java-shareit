package ru.practicum.shareit.exception;

public class OwnerPermissionException extends RuntimeException {
    public  OwnerPermissionException(String message) {
        super(message);
    }
}
