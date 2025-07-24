package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.ItemNotOwnedByUserException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final Map<Long, Item> items = new HashMap<>();
    private long idCounter = 1;
    private final UserService userService;

    public ItemServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Item create(Long userId, Item item) {
        User owner = userService.getById(userId);
        item.setOwner(owner);
        item.setId(idCounter++);
        items.put(item.getId(), item);
        log.info("Created item: {}", item);
        return item;
    }

    @Override
    public Item update(Long userId, Long itemId, Item itemUpdateData) {
        Item existingItem = getById(itemId);
        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ItemNotOwnedByUserException("User with id " + userId + " is not the owner of item " + itemId);
        }
        if (itemUpdateData.getName() != null) {
            existingItem.setName(itemUpdateData.getName());
        }
        if (itemUpdateData.getDescription() != null) {
            existingItem.setDescription(itemUpdateData.getDescription());
        }
        if (itemUpdateData.getAvailable() != null) {
            existingItem.setAvailable(itemUpdateData.getAvailable());
        }
        existingItem.setRequestId(itemUpdateData.getRequestId());
        items.put(itemId, existingItem);
        log.info("Updated item: {}", existingItem);
        return existingItem;
    }

    @Override
    public Item getById(Long itemId) {
        Item item = items.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Item not found with id: " + itemId);
        }
        return item;
    }

    @Override
    public List<Item> getAllByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        String searchText = text.toLowerCase().trim();
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(searchText) ||
                        item.getDescription().toLowerCase().contains(searchText))
                .collect(Collectors.toList());
    }
}