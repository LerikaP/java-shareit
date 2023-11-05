package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoRequest {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;

    public ItemDtoRequest(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }
}
