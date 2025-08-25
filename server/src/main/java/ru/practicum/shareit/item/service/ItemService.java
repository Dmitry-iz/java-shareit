package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, CreateItemRequestDto itemDto);

    ItemDto update(Long userId, Long itemId, UpdateItemRequestDto itemDto);

    ItemDto getById(Long itemId, Long userId);

    List<ItemDto> getAllByOwner(Long ownerId);

    List<ItemDto> search(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}