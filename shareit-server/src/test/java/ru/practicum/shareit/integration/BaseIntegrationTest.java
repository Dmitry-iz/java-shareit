package ru.practicum.shareit.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected BookingRepository bookingRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected ItemRequestRepository itemRequestRepository;

    protected User user1;
    protected User user2;
    protected User user3;
    protected Item item1;
    protected Item item2;
    protected Item item3;
    protected Booking booking1;
    protected Booking booking2;
    protected Comment comment1;
    protected ItemRequest request1;
    protected ItemRequest request2;

    @BeforeEach
    void setUp() {
        // Spring автоматически откатывает транзакцию благодаря @Transactional
        // Просто создаем тестовые данные

        user1 = createUser("User1", "user1@email.com");
        user2 = createUser("User2", "user2@email.com");
        user3 = createUser("User3", "user3@email.com");

        request1 = createItemRequest("Need item 1", user2);
        request2 = createItemRequest("Need item 2", user3);

        item1 = createItem("Item1", "Description1", true, user1, null);
        item2 = createItem("Item2", "Description2", true, user2, request1);
        item3 = createItem("Item3", "Description3", false, user3, request2);

        booking1 = createBooking(
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1),
                item1,
                user2,
                BookingStatus.APPROVED
        );

        booking2 = createBooking(
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2),
                item1,
                user3,
                BookingStatus.WAITING
        );

        comment1 = createComment("Great item!", item1, user2);
    }

    protected User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    protected Item createItem(String name, String description, Boolean available, User owner, ItemRequest request) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(available);
        item.setOwner(owner);
        item.setRequestId(request);
        return itemRepository.save(item);
    }

    protected Booking createBooking(LocalDateTime start, LocalDateTime end, Item item, User booker, BookingStatus status) {
        Booking booking = new Booking();
        booking.setStart(start);
        booking.setEnd(end);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }

    protected Comment createComment(String text, Item item, User author) {
        Comment comment = new Comment();
        comment.setText(text);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    protected ItemRequest createItemRequest(String description, User requester) {
        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(request);
    }
}