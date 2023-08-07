package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item addItem(Item item);

    Item updateItem(Item item);

    Item getItemById(int id);

    List<Item> getAllItemsByUserId(int userId);

    void deleteItemById(int id);

    List<Item> searchItem(String text);
}
