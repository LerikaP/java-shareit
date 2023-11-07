package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDtoWithRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.CustomPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestResponseDto addItemRequest(ItemRequestDto itemRequestDto, long userId) {
        User requestor = findUserForRequest(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, requestor);
        itemRequest = itemRequestRepository.save(itemRequest);
        List<ItemDtoWithRequest> items = findItems(itemRequest.getId());
        return ItemRequestMapper.itemRequestResponseDto(itemRequest, items);
    }

    @Override
    public ItemRequestResponseDto getItemRequestById(long id, long userId) {
        findUserForRequest(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Запрос c id %s не найден", id)));
        List<ItemDtoWithRequest> items = findItems(itemRequest.getId());
        return ItemRequestMapper.itemRequestResponseDto(itemRequest, items);
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequestsByUserId(long userId) {
        findUserForRequest(userId);
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdOrderByCreatedDesc(userId);
        return requests
                .stream()
                .map(request -> ItemRequestMapper.itemRequestResponseDto(request, findItems(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestResponseDto> getAllRequests(long userId, int from, int size) {
        findUserForRequest(userId);
        PageRequest pageRequest = new CustomPageRequest(from, size,Sort.unsorted());
        List<ItemRequest> requests = itemRequestRepository.findByRequestor_IdIsNotOrderByCreatedDesc(userId,
                pageRequest);
        return requests
                .stream()
                .map(request -> ItemRequestMapper.itemRequestResponseDto(request, findItems(request.getId())))
                .collect(Collectors.toList());
    }

    private List<ItemDtoWithRequest> findItems(long id) {
        return itemRepository.findByRequest_IdOrderById(id)
                .stream()
                .map(ItemMapper::toItemDtoWithRequest)
                .collect(Collectors.toList());
    }

    private User findUserForRequest(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %s не найден", userId)));
    }
}
