package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerPermissionException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

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

    @Override
    public ItemDto addItem(ItemDto itemDto, long userId) {
        User owner = getUserForItem(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

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
            lastBooking = bookings
                    .stream()
                    .sorted(orderByStartDesc)
                    .filter(x -> x.getStart().isBefore(currentTime))
                    .findFirst()
                    .orElse(null);
            nextBooking = bookings
                    .stream()
                    .sorted(orderByStartAsc)
                    .filter(x -> x.getStart().isAfter(currentTime))
                    .findFirst()
                    .orElse(null);
        }
        List<CommentResponseDto> comments = commentRepository.findByItem_IdOrderByCreatedDesc(id)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
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
    public List<ItemDtoWithBooking> getAllItemsByUserId(long userId) {
        getUserForItem(userId);
        List<ItemDtoWithBooking> items = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        for (Item item : itemRepository.findByOwner_IdOrderById(userId)) {
            List<Booking> bookings = bookingRepository.findByItem_IdAndStatus(item.getId(), BookingStatus.APPROVED);
            Booking lastBooking = bookings
                    .stream()
                    .sorted(orderByStartDesc)
                    .filter(x -> x.getStart().isBefore(currentTime))
                    .findFirst()
                    .orElse(null);
            Booking nextBooking = bookings
                    .stream()
                    .sorted(orderByStartAsc)
                    .filter(x -> x.getStart().isAfter(currentTime))
                    .findFirst()
                    .orElse(null);
            List<CommentResponseDto> comments = commentRepository.findByItem_IdOrderByCreatedDesc(userId)
                    .stream()
                    .map(CommentMapper::toCommentResponseDto)
                    .collect(Collectors.toList());
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

    @Override
    public void deleteItemById(long id) {
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Item> searchedItems = itemRepository.search(text);
        return searchedItems
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

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
