package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndBeforeStartValidator implements ConstraintValidator<EndBeforeStart, BookItemRequestDto> {

    @Override
    public void initialize(EndBeforeStart endBeforeStart) {
    }

    @Override
    public boolean isValid(BookItemRequestDto requestDto, ConstraintValidatorContext constraintValidatorContext) {
        if (requestDto.getStart() != null && requestDto.getEnd() != null) {
            return requestDto.getStart().isBefore(requestDto.getEnd());
        }
        return false;
    }
}
