package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDtoRequest itemDto, long userId);

    ItemDto updateItem(ItemDto itemDto, long id, long userId);

    ItemDtoWithBooking getItemById(long id, long userId);

    List<ItemDtoWithBooking> getAllItemsByUserId(long userId, int from, int size);

    void deleteItemById(long id);

    List<ItemDto> searchItem(String text, int from, int size);

    CommentResponseDto addComment(long itemId, CommentRequestDto commentRequestDto, long userId);
}
