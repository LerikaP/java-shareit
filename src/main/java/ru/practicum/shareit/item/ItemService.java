package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, int userId);

    ItemDto updateItem(ItemDto itemDto, int id, int userId);

    ItemDto getItemById(int id);

    List<ItemDto> getAllItemsByUserId(int userId);

    void deleteItemById(int id);

    List<ItemDto> searchItem(String text);
}
