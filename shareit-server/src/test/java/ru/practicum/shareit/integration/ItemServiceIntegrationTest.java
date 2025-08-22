package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@Transactional
class ItemServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Test
    void createItem_ShouldCreateItemSuccessfully() {
        // Given
        CreateItemRequestDto requestDto = new CreateItemRequestDto(
                "New Item",
                "New Description",
                true,
                null
        );

        // When
        ItemDto result = itemService.create(user1.getId(), requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Item");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void createItem_WithRequest_ShouldCreateItemWithRequest() {
        // Given
        CreateItemRequestDto requestDto = new CreateItemRequestDto(
                "Requested Item",
                "For request",
                true,
                request1.getId()
        );

        // When
        ItemDto result = itemService.create(user1.getId(), requestDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Requested Item");
    }

    @Test
    void createItem_WithNonExistentUser_ShouldThrowException() {
        // Given
        CreateItemRequestDto requestDto = new CreateItemRequestDto(
                "New Item",
                "Description",
                true,
                null
        );

        // When & Then
        assertThrows(UserNotFoundException.class, () ->
                itemService.create(999L, requestDto)
        );
    }

    @Test
    void updateItem_ShouldUpdateItemSuccessfully() {
        // Given
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto(
                "Updated Name",
                "Updated Description",
                false,
                null
        );

        // When
        ItemDto result = itemService.update(user1.getId(), item1.getId(), updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateItem_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        // Given
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto(
                null,
                "Only description updated",
                null,
                null
        );

        // When
        ItemDto result = itemService.update(user1.getId(), item1.getId(), updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Item1"); // unchanged
        assertThat(result.getDescription()).isEqualTo("Only description updated");
        assertThat(result.getAvailable()).isTrue(); // unchanged
    }

    @Test
    void updateItem_ByNonOwner_ShouldThrowException() {
        // Given
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto(
                "Updated",
                "Updated",
                true,
                null
        );

        // When & Then
        assertThrows(ItemNotOwnedByUserException.class, () ->
                itemService.update(user2.getId(), item1.getId(), updateDto)
        );
    }

    @Test
    void updateItem_NonExistentItem_ShouldThrowException() {
        // Given
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto(
                "Updated",
                "Updated",
                true,
                null
        );

        // When & Then
        assertThrows(ItemNotFoundException.class, () ->
                itemService.update(user1.getId(), 999L, updateDto)
        );
    }

    @Test
    void getItemById_AsOwner_ShouldReturnItemWithBookings() {
        // When
        ItemDto result = itemService.getById(item1.getId(), user1.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getItemById_AsNonOwner_ShouldReturnItemWithoutBookings() {
        // When
        ItemDto result = itemService.getById(item1.getId(), user3.getId());

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getItemById_NonExistentItem_ShouldThrowException() {
        // When & Then
        assertThrows(ItemNotFoundException.class, () ->
                itemService.getById(999L, user1.getId())
        );
    }

    @Test
    void getAllByOwner_ShouldReturnAllOwnerItems() {
        // When
        List<ItemDto> result = itemService.getAllByOwner(user1.getId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(item1.getId());
    }

    @Test
    void searchItems_ShouldReturnAvailableItemsMatchingText() {
        // When
        List<ItemDto> result = itemService.search("item1");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(item1.getId());
    }

    @Test
    void searchItems_WithBlankText_ShouldReturnEmptyList() {
        // When
        List<ItemDto> result = itemService.search("");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void searchItems_UnavailableItem_ShouldNotReturn() {
        // When
        List<ItemDto> result = itemService.search("item3");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    void addComment_ShouldAddCommentSuccessfully() {
        // Given
        CommentDto commentDto = new CommentDto(null, "Excellent item!", null, null);

        // When
        CommentDto result = itemService.addComment(user2.getId(), item1.getId(), commentDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Excellent item!");
        assertThat(result.getAuthorName()).isEqualTo("User2");
    }

    @Test
    void addComment_ByUserWhoNeverBooked_ShouldThrowException() {
        // Given
        CommentDto commentDto = new CommentDto(null, "Comment", null, null);

        // When & Then
        assertThrows(RuntimeException.class, () ->
                itemService.addComment(user1.getId(), item1.getId(), commentDto)
        );
    }
}
