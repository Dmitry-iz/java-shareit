package ru.practicum.shareit.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
class ItemServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Test
    void createItem_ShouldCreateItemSuccessfully() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto(
                "New Item",
                "New Description",
                true,
                null
        );

        ItemDto result = itemService.create(user1.getId(), requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Item");
        assertThat(result.getDescription()).isEqualTo("New Description");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void createItem_WithRequest_ShouldCreateItemWithRequest() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto(
                "Requested Item",
                "For request",
                true,
                request1.getId()
        );

        ItemDto result = itemService.create(user1.getId(), requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Requested Item");
    }

    @Test
    void createItem_WithNonExistentUser_ShouldThrowException() {
        CreateItemRequestDto requestDto = new CreateItemRequestDto(
                "New Item",
                "Description",
                true,
                null
        );

        assertThrows(UserNotFoundException.class, () ->
                itemService.create(999L, requestDto)
        );
    }

    @Test
    void updateItem_ShouldUpdateItemSuccessfully() {
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto(
                "Updated Name",
                "Updated Description",
                false,
                null
        );

        ItemDto result = itemService.update(user1.getId(), item1.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated Name");
        assertThat(result.getDescription()).isEqualTo("Updated Description");
        assertThat(result.getAvailable()).isFalse();
    }

    @Test
    void updateItem_WithPartialData_ShouldUpdateOnlyProvidedFields() {
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto(
                null,
                "Only description updated",
                null,
                null
        );

        ItemDto result = itemService.update(user1.getId(), item1.getId(), updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Item1");
        assertThat(result.getDescription()).isEqualTo("Only description updated");
        assertThat(result.getAvailable()).isTrue();
    }

    @Test
    void updateItem_ByNonOwner_ShouldThrowException() {
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto(
                "Updated",
                "Updated",
                true,
                null
        );

        assertThrows(ItemNotOwnedByUserException.class, () ->
                itemService.update(user2.getId(), item1.getId(), updateDto)
        );
    }

    @Test
    void updateItem_NonExistentItem_ShouldThrowException() {
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto(
                "Updated",
                "Updated",
                true,
                null
        );

        assertThrows(ItemNotFoundException.class, () ->
                itemService.update(user1.getId(), 999L, updateDto)
        );
    }

    @Test
    void getItemById_AsOwner_ShouldReturnItemWithBookings() {
        ItemDto result = itemService.getById(item1.getId(), user1.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getLastBooking()).isNotNull();
        assertThat(result.getNextBooking()).isNotNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getItemById_AsNonOwner_ShouldReturnItemWithoutBookings() {
        ItemDto result = itemService.getById(item1.getId(), user3.getId());

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(item1.getId());
        assertThat(result.getLastBooking()).isNull();
        assertThat(result.getNextBooking()).isNull();
        assertThat(result.getComments()).hasSize(1);
    }

    @Test
    void getItemById_NonExistentItem_ShouldThrowException() {
        assertThrows(ItemNotFoundException.class, () ->
                itemService.getById(999L, user1.getId())
        );
    }

    @Test
    void getAllByOwner_ShouldReturnAllOwnerItems() {
        List<ItemDto> result = itemService.getAllByOwner(user1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(item1.getId());
    }

    @Test
    void searchItems_ShouldReturnAvailableItemsMatchingText() {
        List<ItemDto> result = itemService.search("item1");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(item1.getId());
    }

    @Test
    void searchItems_WithBlankText_ShouldReturnEmptyList() {
        List<ItemDto> result = itemService.search("");

        assertThat(result).isEmpty();
    }

    @Test
    void searchItems_UnavailableItem_ShouldNotReturn() {
        List<ItemDto> result = itemService.search("item3");

        assertThat(result).isEmpty();
    }

    @Test
    void addComment_ShouldAddCommentSuccessfully() {
        CommentDto commentDto = new CommentDto(null, "Excellent item!", null, null);

        CommentDto result = itemService.addComment(user2.getId(), item1.getId(), commentDto);

        assertThat(result).isNotNull();
        assertThat(result.getText()).isEqualTo("Excellent item!");
        assertThat(result.getAuthorName()).isEqualTo("User2");
    }

    @Test
    void addComment_ByUserWhoNeverBooked_ShouldThrowException() {
        CommentDto commentDto = new CommentDto(null, "Comment", null, null);

        assertThrows(RuntimeException.class, () ->
                itemService.addComment(user1.getId(), item1.getId(), commentDto)
        );
    }
}
