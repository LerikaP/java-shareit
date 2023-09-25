package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(long id);

    List<Item> getAllItemsByUserId(long userId);

    void deleteItemById(long id);

    List<Item> searchItem(String text);
}
