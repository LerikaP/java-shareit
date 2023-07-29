package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.OwnerPermissionException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto addItem(ItemDto itemDto, int userId) {
        User owner = UserMapper.toUser(userService.getUserById(userId));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemStorage.addItem(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, int id, int userId) {
        userService.getUserById(userId);
        Item item = itemStorage.getItemById(id);
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
        if (item.getOwner().getId() != userId) {
            throw new OwnerPermissionException("Редактировать вещь может только её владелец");
        } else {
            if (name != null) {
                item.setName(name);
            }
            if (description != null) {
                item.setDescription(description);
            }
            if (available != null) {
                item.setAvailable(available);
            }
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(item));
    }

    @Override
    public ItemDto getItemById(int id) {
        return ItemMapper.toItemDto(itemStorage.getItemById(id));
    }

    @Override
    public List<ItemDto> getAllItemsByUserId(int userId) {
        userService.getUserById(userId);
        List<Item> itemsByUserId = itemStorage.getAllItemsByUserId(userId);
        return itemsByUserId
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItemById(int id) {
        itemStorage.deleteItemById(id);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> searchedItems = itemStorage.searchItem(text);
        return searchedItems
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
