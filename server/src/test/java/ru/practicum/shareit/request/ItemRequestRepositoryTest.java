package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.utils.CustomPageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository requestRepository;
    private static User user;
    private static User secondUser;
    private static ItemRequest request;
    private static ItemRequest secondRequest;
    private static CustomPageRequest pageRequest;

    @BeforeEach
    void setUp() {
        pageRequest = new CustomPageRequest(0, 10, Sort.unsorted());

        user = userRepository.save(new User(1L, "user 1", "user1@email.com"));
        request = requestRepository.save(new ItemRequest(1L, "description", user, LocalDateTime.now()));
        secondUser = userRepository.save(new User(2L, "user 2", "user2@email.com"));
        secondRequest = requestRepository.save(new ItemRequest(2L, "description 2", secondUser,
                LocalDateTime.now()));
    }

    @Test
    void should_find_by_requestor_id() {
        final List<ItemRequest> requests = requestRepository.findByRequestor_IdOrderByCreatedDesc(user.getId());

        assertEquals(1, requests.size());
        assertEquals("description", requests.get(0).getDescription());
    }

    @Test
    void should_find_by_not_requestor_id() {
        final List<ItemRequest> requests = requestRepository.findByRequestor_IdIsNotOrderByCreatedDesc(user.getId(),
                pageRequest);

        assertEquals(1, requests.size());
        assertEquals("description 2", requests.get(0).getDescription());
    }
}
