package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmailException(final DuplicateEmailException e) {
        String eMessage = e.getMessage();
        return new ErrorResponse("Пользователь с указанным email уже существует", eMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        String eMessage = e.getMessage();
        return new ErrorResponse("Запрашиваемый ресурс не найден", eMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleOwnerPermissionException(final OwnerPermissionException e) {
        String eMessage = e.getMessage();
        return new ErrorResponse("Редактировать вещь может только её владелец", eMessage);
    }
}
