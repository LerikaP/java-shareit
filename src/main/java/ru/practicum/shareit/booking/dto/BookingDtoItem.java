package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoItem {
    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private long bookerId;
    private BookingStatus status;
}
