package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestWithItemsDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> getAllByUser(Long userId);

    List<ItemRequestWithItemsDto> getAll(Long userId, Integer from, Integer size);

    ItemRequestWithItemsDto getById(Long userId, Long requestId);
}
