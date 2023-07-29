package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("{id}")
    public ItemDto getItemById(@PathVariable int id) {
        return itemService.getItemById(id);
    }

    @GetMapping
    public List<ItemDto> getAllItemsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping
    public ItemDto addItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@PathVariable int id, @Validated(Update.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") int userId) {
        return itemService.updateItem(itemDto, id, userId);
    }

    @DeleteMapping("{id}")
    public void deleteItem(@PathVariable int id) {
        itemService.deleteItemById(id);
    }
}
