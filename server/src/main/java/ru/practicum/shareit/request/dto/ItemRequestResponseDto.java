package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.dto.ItemDtoWithRequest;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestResponseDto {
    private Long id;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoWithRequest> items;
}
