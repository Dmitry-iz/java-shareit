package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.ItemClient;
import ru.practicum.shareit.dto.item.CommentDto;
import ru.practicum.shareit.dto.item.CreateItemRequestDto;
import ru.practicum.shareit.dto.item.UpdateItemRequestDto;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody @Valid CreateItemRequestDto itemDto) {
        log.info("Creating item {}, userId={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid UpdateItemRequestDto itemDto) {
        log.info("Updating item {}, userId={}, itemId={}", itemDto, userId, itemId);
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Get all items for user {}", userId);
        return itemClient.getUsersItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam String text,
                                              @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Search items with text: {}", text);
        return itemClient.searchItems(text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody @Valid CommentDto commentDto) {
        log.info("Adding comment to item {}, userId={}", itemId, userId);
        return itemClient.addComment(userId, itemId, commentDto);
    }
}
