package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class MapperIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private BookingMapper bookingMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private ItemRequestMapper itemRequestMapper;

    @Test
    void userMapper_ShouldMapCorrectly() {
        var createDto = new ru.practicum.shareit.user.dto.CreateUserRequestDto("Test User", "test@email.com");
        var user = userMapper.fromCreateDto(createDto);

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Test User");
        assertThat(user.getEmail()).isEqualTo("test@email.com");
    }

    @Test
    void itemMapper_ShouldMapFromCreateDto() {
        var createDto = new ru.practicum.shareit.item.dto.CreateItemRequestDto(
                "Test Item", "Test Description", true, null
        );

        var item = itemMapper.fromCreateDto(createDto, user1);

        assertThat(item).isNotNull();
        assertThat(item.getName()).isEqualTo("Test Item");
        assertThat(item.getDescription()).isEqualTo("Test Description");
        assertThat(item.getAvailable()).isTrue();
        assertThat(item.getOwner()).isEqualTo(user1);
        assertThat(item.getRequestId()).isNull();
    }

    @Test
    void bookingMapper_ShouldUpdateStatusCorrectly() {
        var booking = new ru.practicum.shareit.booking.model.Booking();
        booking.setStatus(BookingStatus.WAITING);

        bookingMapper.updateStatus(true, booking);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.APPROVED);

        bookingMapper.updateStatus(false, booking);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void commentMapper_ShouldMapToDtoCorrectly() {
        var commentDto = commentMapper.toDto(comment1);

        assertThat(commentDto).isNotNull();
        assertThat(commentDto.getText()).isEqualTo("Great item!");
        assertThat(commentDto.getAuthorName()).isEqualTo("User2");
    }

    @Test
    void itemRequestMapper_ShouldMapFromDtoCorrectly() {
        var requestDto = new ru.practicum.shareit.request.dto.ItemRequestDto("Test request description");
        var request = itemRequestMapper.fromDto(requestDto, user1, LocalDateTime.now());

        assertThat(request).isNotNull();
        assertThat(request.getDescription()).isEqualTo("Test request description");
        assertThat(request.getRequester()).isEqualTo(user1);
    }

    @Test
    void itemMapper_ShouldUpdateItemFromDto() {
        var updateDto = new ru.practicum.shareit.item.dto.UpdateItemRequestDto(
                "Updated Name", "Updated Description", false, request1.getId()
        );

        var originalItem = createItem("Original", "Original Desc", true, user1, null);
        itemMapper.updateItemFromDto(updateDto, originalItem);

        assertThat(originalItem.getName()).isEqualTo("Updated Name");
        assertThat(originalItem.getDescription()).isEqualTo("Updated Description");
        assertThat(originalItem.getAvailable()).isFalse();
        assertThat(originalItem.getRequestId()).isNotNull();
        assertThat(originalItem.getRequestId().getId()).isEqualTo(request1.getId());
    }

    @Test
    void itemMapper_ShouldUpdateItemFromDto_WithPartialData() {
        var updateDto = new ru.practicum.shareit.item.dto.UpdateItemRequestDto(
                null, "Only description updated", null, null
        );

        var originalItem = createItem("Original", "Original Desc", true, user1, null);
        itemMapper.updateItemFromDto(updateDto, originalItem);

        assertThat(originalItem.getName()).isEqualTo("Original");
        assertThat(originalItem.getDescription()).isEqualTo("Only description updated");
        assertThat(originalItem.getAvailable()).isTrue();
        assertThat(originalItem.getRequestId()).isNull();
    }

    @Test
    void itemMapper_ShouldMapToDtoWithBookings() {
        LocalDateTime now = LocalDateTime.now();
        var lastBooking = createBooking(
                now.minusDays(2), now.minusDays(1), item1, user2, BookingStatus.APPROVED
        );
        var nextBooking = createBooking(
                now.plusDays(1), now.plusDays(2), item1, user3, BookingStatus.WAITING
        );

        var comments = List.of(comment1);

        var result = itemMapper.toDtoWithBookings(item1, List.of(lastBooking), List.of(nextBooking),
                comments.stream().map(commentMapper::toDto).collect(Collectors.toList()));

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void itemMapper_ShouldMapToDtoWithBookings_WhenNoBookings() {
        var result = itemMapper.toDtoWithBookings(item2, List.of(), List.of(), List.of());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item2.getId());
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isEmpty();
    }

    @Test
    void itemMapper_ShouldMapToSimpleItemDto() {
        var result = itemMapper.toItemDto(item1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getName()).isEqualTo(item1.getName());
        assertThat(result.getDescription()).isEqualTo(item1.getDescription());
        assertThat(result.getAvailable()).isEqualTo(item1.getAvailable());
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).isNull();
    }

    @Test
    void userMapper_ShouldUpdateFromDto() {
        var updateDto = new ru.practicum.shareit.user.dto.UpdateUserRequestDto(
                "updated@email.com", "Updated Name"
        );

        var originalUser = createUser("Original", "original@email.com");
        userMapper.updateFromDto(updateDto, originalUser);

        assertThat(originalUser.getName()).isEqualTo("Updated Name");
        assertThat(originalUser.getEmail()).isEqualTo("updated@email.com");
    }

    @Test
    void userMapper_ShouldUpdateFromDto_WithPartialData() {
        var updateDto = new ru.practicum.shareit.user.dto.UpdateUserRequestDto(
                null, "Only Name Updated"
        );

        var originalUser = createUser("Original", "original@email.com");
        userMapper.updateFromDto(updateDto, originalUser);

        assertThat(originalUser.getName()).isEqualTo("Only Name Updated");
        assertThat(originalUser.getEmail()).isEqualTo("original@email.com");
    }

    @Test
    void bookingMapper_ShouldMapToDto() {
        var bookingDto = bookingMapper.toDto(booking1);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking1.getId());
        assertThat(bookingDto.getStart()).isEqualTo(booking1.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking1.getEnd());
        assertThat(bookingDto.getStatus()).isEqualTo(booking1.getStatus());
        assertThat(bookingDto.getBooker().getId()).isEqualTo(booking1.getBooker().getId());
        assertThat(bookingDto.getItem().getId()).isEqualTo(booking1.getItem().getId());
    }

    @Test
    void commentMapper_ShouldMapFromDto() {
        var commentDto = new ru.practicum.shareit.item.dto.CommentDto(
                null, "Test comment", null, null
        );

        var comment = commentMapper.fromDto(commentDto, item1, user1);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo("Test comment");
        assertThat(comment.getItem()).isEqualTo(item1);
        assertThat(comment.getAuthor()).isEqualTo(user1);
        assertThat(comment.getCreated()).isNotNull();
    }

    @Test
    void itemRequestMapper_ShouldMapToDtoList() {
        var requests = List.of(request1, request2);
        var result = itemRequestMapper.toDtoList(requests);

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ru.practicum.shareit.request.dto.ItemRequestWithItemsDto::getId)
                .containsExactlyInAnyOrder(request1.getId(), request2.getId());
    }

    @Test
    void itemRequestMapper_ShouldMapToSimpleDto() {
        var result = itemRequestMapper.toSimpleDto(request1);

        assertThat(result).isNotNull();
        assertThat(result.getDescription()).isEqualTo(request1.getDescription());
    }
}