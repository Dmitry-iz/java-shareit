package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public static Item fromCreateDto(CreateItemRequestDto dto) {
        return new Item(null, dto.getName(), dto.getDescription(), dto.getAvailable(), null, dto.getRequestId());
    }

    public static Item fromUpdateDto(UpdateItemRequestDto dto) {
        return new Item(null, dto.getName(), dto.getDescription(), dto.getAvailable(), null, dto.getRequestId());
    }
}
