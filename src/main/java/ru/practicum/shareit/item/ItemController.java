package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("{id}")
    public ItemDtoWithBooking getItemById(@PathVariable long id, @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getItemById(id, userId);
    }

    @GetMapping
    public List<ItemDtoWithBooking> getAllItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.getAllItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchItem(text);
    }

    @PostMapping
    public ItemDto addItem(@Validated(Create.class) @RequestBody ItemDto itemDto,
                           @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@PathVariable long id, @Validated(Update.class) @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.updateItem(itemDto, id, userId);
    }

    @DeleteMapping("{id}")
    public void deleteItem(@PathVariable long id) {
        itemService.deleteItemById(id);
    }

    @PostMapping("{id}/comment")
    public CommentResponseDto addComment(@PathVariable long id, @Valid @RequestBody CommentRequestDto commentRequestDto,
                                         @RequestHeader(USER_ID_HEADER) long userId) {
        return itemService.addComment(id, commentRequestDto, userId);
    }
}
