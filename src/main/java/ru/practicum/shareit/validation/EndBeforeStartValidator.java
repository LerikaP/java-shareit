package ru.practicum.shareit.validation;

import ru.practicum.shareit.utils.Interval;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndBeforeStartValidator implements ConstraintValidator<EndBeforeStart, Interval> {

    @Override
    public void initialize(EndBeforeStart endBeforeStart) {
    }

    @Override
    public boolean isValid(Interval interval, ConstraintValidatorContext constraintValidatorContext) {
        if (interval.getStart() != null && interval.getEnd() != null) {
            return interval.getStart().isBefore(interval.getEnd());
        }
        return false;
    }
}
