package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(itemDto.getId(), itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable());
    }

    public static Item toItemFromRequestItem(ItemDtoRequest requestDto) {
        Item item = new Item();
        item.setName(requestDto.getName());
        item.setDescription(requestDto.getDescription());
        item.setAvailable(requestDto.getAvailable());
        return item;
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

    public static ItemDtoWithRequest toItemDtoWithRequest(Item item) {
        ItemDtoWithRequest itemDtoWithRequest = new ItemDtoWithRequest();
        itemDtoWithRequest.setId(item.getId());
        itemDtoWithRequest.setName(item.getName());
        itemDtoWithRequest.setDescription(item.getDescription());
        itemDtoWithRequest.setAvailable(item.getAvailable());
        itemDtoWithRequest.setRequestId(item.getRequest().getId());
        return itemDtoWithRequest;
    }
}
