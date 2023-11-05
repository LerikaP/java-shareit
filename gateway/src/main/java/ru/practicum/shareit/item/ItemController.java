package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.utils.Create;
import ru.practicum.shareit.utils.Update;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(USER_ID_HEADER) long userId,
                                                   @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                       Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10")
                                                       Integer size) {
        log.info("Get items by userId={}, from={}, size={}", userId, from, size);
        return itemClient.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(USER_ID_HEADER) long userId,
                                          @PathVariable long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0")
                                                 Integer from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                             @RequestParam String text) {
        log.info("Search items by text {}", text);
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(USER_ID_HEADER) long userId,
                                          @RequestBody @Validated(Create.class) ItemRequestDto requestDto) {
        log.info("Creating item {}, userId={}", requestDto, userId);
        return itemClient.addItem(userId, requestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId,
                                             @Validated(Update.class) @RequestBody ItemRequestDto requestDto,
                                             @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Update item {}, userId={}", requestDto, userId);
        return itemClient.updateItem(itemId, userId, requestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable long itemId) {
        log.info("Delete item {}", itemId);
        itemClient.deleteItem(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable long itemId,
                                             @Valid @RequestBody CommentRequestDto requestDto,
                                             @RequestHeader(USER_ID_HEADER) long userId) {
        log.info("Creating comment {}, itemId={}", requestDto, itemId);
        return itemClient.addComment(itemId, userId, requestDto);
    }

}
