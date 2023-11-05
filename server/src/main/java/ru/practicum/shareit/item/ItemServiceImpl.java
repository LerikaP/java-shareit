package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerPermissionException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.CustomPageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto addItem(ItemDtoRequest itemDto, long userId) {
        User owner = getUserForItem(userId);
        Item item = ItemMapper.toItemFromRequestItem(itemDto);
        item.setOwner(owner);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format(
                            "Запрос с id %s не найден", itemDto.getRequestId())));
            item.setRequest(request);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long userId) {
        User user = getUserForItem(userId);
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->  new NotFoundException(String.format("Вещь с id %s не найдена", id)));
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
        if (item.getOwner().getId() != userId) {
            throw new OwnerPermissionException(
                    String.format("Пользователь %s не является владельцем вещи", user.getName()));
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
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithBooking getItemById(long id, long userId) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() ->  new NotFoundException(String.format("Вещь с id %s не найдена", id)));
        Booking lastBooking = null;
        Booking nextBooking = null;
        LocalDateTime currentTime = LocalDateTime.now();
        if (item.getOwner().getId() == userId) {
            List<Booking> bookings = bookingRepository.findByItem_IdAndStatus(id, BookingStatus.APPROVED);
            lastBooking = findLastBooking(bookings, currentTime);
            nextBooking = findNextBooking(bookings, currentTime);
        }
        List<CommentResponseDto> comments = findComments(id);
        ItemDtoWithBooking itemDtoWithBooking = ItemMapper.itemDtoWithBooking(item, comments);
        if (lastBooking != null) {
            itemDtoWithBooking.setLastBooking(BookingMapper.toBookingDtoItem(lastBooking));
        }
        if (nextBooking != null) {
            itemDtoWithBooking.setNextBooking(BookingMapper.toBookingDtoItem(nextBooking));
        }
        return itemDtoWithBooking;
    }

    @Override
    public List<ItemDtoWithBooking> getAllItemsByUserId(long userId, int from, int size) {
        getUserForItem(userId);
        List<ItemDtoWithBooking> items = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.unsorted());
        for (Item item : itemRepository.findByOwner_IdOrderById(userId, pageRequest)) {
            List<Booking> bookings = bookingRepository.findByItem_IdAndStatus(item.getId(), BookingStatus.APPROVED);
            Booking lastBooking = findLastBooking(bookings, currentTime);
            Booking nextBooking = findNextBooking(bookings, currentTime);
            List<CommentResponseDto> comments = findComments(item.getId());
            ItemDtoWithBooking itemDtoWithBooking = ItemMapper.itemDtoWithBooking(item, comments);
            if (lastBooking != null) {
                itemDtoWithBooking.setLastBooking(BookingMapper.toBookingDtoItem(lastBooking));
            }
            if (nextBooking != null) {
                itemDtoWithBooking.setNextBooking(BookingMapper.toBookingDtoItem(nextBooking));
            }
            items.add(itemDtoWithBooking);
        }
        return items;
    }

    @Transactional
    @Override
    public void deleteItemById(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItem(String text, int from, int size) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        PageRequest pageRequest = new CustomPageRequest(from, size, Sort.unsorted());
        List<Item> searchedItems = itemRepository.search(text, pageRequest);
        return searchedItems
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentResponseDto addComment(long itemId, CommentRequestDto commentRequestDto, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() ->  new NotFoundException(String.format("Вещь с id %s не найдена", itemId)));
        User user = getUserForItem(userId);
        List<Booking> userBookings = bookingRepository.findByBooker_IdAndItem_IdAndEndIsBeforeAndStatusOrderByStart(
                userId, itemId, LocalDateTime.now(), BookingStatus.APPROVED);
        if (userBookings.isEmpty()) {
            throw new BookingValidationException(
                    String.format("Пользователь с id %s не бронировал вещь %s", userId, item.getName()));
        }
        Comment comment = CommentMapper.toComment(commentRequestDto, item, user);
        comment = commentRepository.save(comment);
        return CommentMapper.toCommentResponseDto(comment);
    }

    private User getUserForItem(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь c id %s не найден", id)));
    }

    private Booking findLastBooking(List<Booking> bookings, LocalDateTime currentTime) {
        return bookings.stream()
                .sorted(orderByStartDesc)
                .filter(x -> x.getStart().isBefore(currentTime))
                .findFirst()
                .orElse(null);
    }

    private Booking findNextBooking(List<Booking> bookings, LocalDateTime currentTime) {
        return bookings.stream()
                .sorted(orderByStartAsc)
                .filter(x -> x.getStart().isAfter(currentTime))
                .findFirst()
                .orElse(null);
    }

    private List<CommentResponseDto> findComments(long id) {
        return commentRepository.findByItem_IdOrderByCreatedDesc(id)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
    }

    private static final Comparator<Booking> orderByStartDesc = (x, y) -> {
        if (x.getStart().isAfter((y.getStart()))) {
            return  -1;
        } else if (x.getStart().isBefore(y.getStart())) {
            return 1;
        } else {
            return 0;
        }
    };

    private static final Comparator<Booking> orderByStartAsc = (x, y) -> {
        if (x.getStart().isAfter((y.getStart()))) {
            return  1;
        } else if (x.getStart().isBefore(y.getStart())) {
            return -1;
        } else {
            return 0;
        }
    };

}
