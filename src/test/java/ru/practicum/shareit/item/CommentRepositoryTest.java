package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;
    private static User user;
    private static Item item;
    private static Comment comment;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(1L, "user 1", "user1@email.com"));
        item = itemRepository.save(new Item(1L, "item 1", "description 1", true));
        comment = commentRepository.save(new Comment(1L, "text", item, user, LocalDateTime.now()));
    }

    @Test
    void should_find_comments_by_item_id() {
        final List<Comment> comments = commentRepository.findByItem_IdOrderByCreatedDesc(item.getId());

        assertEquals(1, comments.size());
        assertEquals("text", comments.get(0).getText());
    }

}
