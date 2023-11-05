package ru.practicum.shareit.booking.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
@Documented
@Constraint(validatedBy = EndBeforeStartValidator.class)
public @interface EndBeforeStart {
    String message() default "Время начала бронирования не может быть позже времени окончания бронирования";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
