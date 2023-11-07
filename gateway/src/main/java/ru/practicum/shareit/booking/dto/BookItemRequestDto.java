package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.validation.EndBeforeStart;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EndBeforeStart
public class BookItemRequestDto {
	@NotNull
	private long itemId;
	@NotNull
	@FutureOrPresent
	private LocalDateTime start;
	@NotNull
	@Future
	private LocalDateTime end;
}
