package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping("{id}")
    public ItemRequestResponseDto getRequestById(@PathVariable long id, @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getItemRequestById(id, userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size,
                                                       @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getAllRequestsByUserId(@RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.getAllRequestsByUserId(userId);
    }

    @PostMapping
    public ItemRequestResponseDto addItemRequest(@RequestBody ItemRequestDto itemRequestDto,
                                                 @RequestHeader(USER_ID_HEADER) long userId) {
        return itemRequestService.addItemRequest(itemRequestDto, userId);
    }
}
