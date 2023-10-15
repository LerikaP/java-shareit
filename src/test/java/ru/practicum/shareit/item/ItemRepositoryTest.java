package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.CustomPageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private static User user;
    private static Item item;
    private static User secondUser;
    private static Item secondItem;
    private static CustomPageRequest pageRequest;

    @BeforeEach
    void setUp() {
        pageRequest = new CustomPageRequest(0, 10, Sort.unsorted());

        user = userRepository.save(new User(1L, "user 1", "user1@email.com"));
        item = itemRepository.save(new Item(1L, "item 1", "description 1", true));
        item.setOwner(user);

        secondUser = userRepository.save(new User(2L, "user 2", "user2@email.com"));
        secondItem = itemRepository.save(new Item(2L, "item 2", "description 2", true));
        secondItem.setOwner(secondUser);
    }

    @Test
    void should_find_items_by_owner_id() {
        final List<Item> items = itemRepository.findByOwner_IdOrderById(user.getId(), pageRequest);

        assertEquals(1, items.size());
        assertEquals("item 1", items.get(0).getName());
    }

    @Test
    void should_search_items() {
        List<Item> items = itemRepository.search("item", pageRequest);

        assertEquals(2, items.size());
        assertEquals("item 1", items.get(0).getName());
        assertEquals("item 2", items.get(1).getName());
    }

    @Test
    void should_find_items_by_request_id() {
        ItemRequest request = requestRepository.save(
                new ItemRequest(1L, "description", user, LocalDateTime.now()));
        item.setRequest(request);
        final List<Item> items = itemRepository.findByRequest_IdOrderById(request.getId());

        assertEquals(1, items.size());
        assertEquals("item 1", items.get(0).getName());
    }
}
