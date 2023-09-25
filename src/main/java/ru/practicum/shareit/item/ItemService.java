package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    ItemDtoWithBooking getItemById(long id, long userId);

    List<ItemDtoWithBooking> getAllItemsByUserId(long userId);

    void deleteItemById(long id);

    List<ItemDto> searchItem(String text);

    CommentResponseDto addComment(long itemId, CommentRequestDto commentRequestDto, long userId);
}
