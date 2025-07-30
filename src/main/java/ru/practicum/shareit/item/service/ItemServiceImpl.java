package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public ItemDto create(Long userId, CreateItemRequestDto itemDto) {
        UserDto userDto = userService.getById(userId);
        User owner = userMapper.fromUserDto(userDto);

        Item item = itemMapper.fromCreateDto(itemDto, owner);
        item.setId(idCounter++);
        items.put(item.getId(), item);

        log.info("Created item: {}", item);
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long userId, Long itemId, UpdateItemRequestDto itemDto) {
        userService.getById(userId);

        Item existingItem = items.get(itemId);
        if (existingItem == null) {
            throw new ItemNotFoundException("Item not found with id: " + itemId);
        }
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ItemNotOwnedByUserException("User with id " + userId + " is not the owner of item " + itemId);
        }

        itemMapper.updateItemFromDto(itemDto, existingItem);

        items.put(itemId, existingItem);
        log.info("Updated item: {}", existingItem);
        return itemMapper.toItemDto(existingItem);
    }

    @Override
    public ItemDto getById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Item not found with id: " + itemId);
        }
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getAllByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text.toLowerCase().trim();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}