package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto addItemRequest(ItemRequestDto itemRequestDto, long userId);

    ItemRequestResponseDto getItemRequestById(long id, long userId);

    List<ItemRequestResponseDto> getAllRequestsByUserId(long userId);

    List<ItemRequestResponseDto> getAllRequests(long userId, int from, int size);

}
