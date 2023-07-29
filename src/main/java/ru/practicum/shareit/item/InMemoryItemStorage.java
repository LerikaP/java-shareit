package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private int userIdGenerator = 1;

    @Override
    public Item addItem(Item item) {
        int id = getNextFreeId();
        item.setId(id);
        items.put(id, item);
        return items.get(id);
    }

    @Override
    public Item updateItem(Item item) {
        int id = item.getId();
        if (items.containsKey(id)) {
            items.put(id, item);
        } else {
            throw new NotFoundException("Вещь с указанным id не найдена");
        }
        return items.get(id);
    }

    @Override
    public Item getItemById(int id) {
        if (items.containsKey(id)) {
            return items.get(id);
        } else {
            throw new NotFoundException("Вещь с указанным id не найдена");
        }
    }

    @Override
    public List<Item> getAllItemsByUserId(int userId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemById(int id) {
        if (items.containsKey(id)) {
            items.remove(id);
        } else {
            throw new NotFoundException("Вещь с указанным id не найдена");
        }
    }

    @Override
    public List<Item> searchItem(String text) {
        return items.values()
                .stream()
                .filter(item -> item.getAvailable()
                        && (item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    private int getNextFreeId() {
        return userIdGenerator++;
    }
}
