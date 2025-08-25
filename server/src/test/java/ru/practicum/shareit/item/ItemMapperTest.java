package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperTest {

    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toItemDto_ShouldMapCorrectly() {
        User owner = new User(1L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Test Item", "Test Description", true, owner, null);

        ItemDto result = mapper.toItemDto(item);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
    }

    @Test
    void toDtoWithBookings_ShouldMapCorrectly() {
        User owner = new User(1L, "Owner", "owner@email.com");
        Item item = new Item(1L, "Test Item", "Test Description", true, owner, null);
        List<Booking> lastBookings = Collections.emptyList();
        List<Booking> nextBookings = Collections.emptyList();
        List<ru.practicum.shareit.item.dto.CommentDto> comments = Collections.emptyList();

        ItemDto result = mapper.toDtoWithBookings(item, lastBookings, nextBookings, comments);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
    }

    @Test
    void fromCreateDto_ShouldMapCorrectly() {
        CreateItemRequestDto dto = new CreateItemRequestDto("Test Item", "Test Description", true, null);
        User owner = new User(1L, "Owner", "owner@email.com");

        Item result = mapper.fromCreateDto(dto, owner);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getDescription(), result.getDescription());
        assertEquals(dto.getAvailable(), result.getAvailable());
        assertEquals(owner, result.getOwner());
    }

    @Test
    void updateItemFromDto_ShouldUpdateOnlyProvidedFields() {
        Item existingItem = new Item(1L, "Original", "Original Desc", true, new User(), null);
        UpdateItemRequestDto updateDto = new UpdateItemRequestDto("Updated", null, false, null);

        mapper.updateItemFromDto(updateDto, existingItem);

        assertEquals("Updated", existingItem.getName());
        assertEquals("Original Desc", existingItem.getDescription());
        assertFalse(existingItem.getAvailable());
    }

    @Test
    void mapRequestId_WithNull_ShouldReturnNull() {
        assertNull(mapper.mapRequestId(null));
    }

    @Test
    void mapRequestId_WithValidId_ShouldReturnItemRequest() {
        ItemRequest result = mapper.mapRequestId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }
}
