package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable());
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }

    public static ItemDtoWithBooking itemDtoWithBooking(Item item, List<CommentResponseDto> comments) {
        ItemDtoWithBooking itemDtoWithBooking = new ItemDtoWithBooking();
        itemDtoWithBooking.setId(item.getId());
        itemDtoWithBooking.setName(item.getName());
        itemDtoWithBooking.setDescription(item.getDescription());
        itemDtoWithBooking.setAvailable(item.getAvailable());
        itemDtoWithBooking.setComments(comments);
        return itemDtoWithBooking;
    }
}
