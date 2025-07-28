package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestBody @Valid CreateItemRequestDto itemDto) {
        Item item = itemMapper.fromCreateDto(itemDto);
        Item createdItem = itemService.create(userId, item);
        return ResponseEntity.ok(itemMapper.toItemDto(createdItem));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid UpdateItemRequestDto itemDto) {
        Item item = itemMapper.fromUpdateDto(itemDto);
        Item updatedItem = itemService.update(userId, itemId, item);
        return ResponseEntity.ok(itemMapper.toItemDto(updatedItem));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId) {
        Item item = itemService.getById(itemId);
        return ResponseEntity.ok(itemMapper.toItemDto(item));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        List<ItemDto> items = itemService.getAllByOwner(userId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        List<ItemDto> items = itemService.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(items);
    }
}