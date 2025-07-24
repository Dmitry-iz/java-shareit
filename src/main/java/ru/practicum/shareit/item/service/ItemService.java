package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item create(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    Item getById(Long itemId);

    List<Item> getAllByOwner(Long ownerId);

    List<Item> search(String text);
}
