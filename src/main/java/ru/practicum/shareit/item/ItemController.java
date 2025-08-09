package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CreateItemRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @RequestBody @Valid CreateItemRequestDto itemDto) {
        return ResponseEntity.ok(itemService.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @PathVariable Long itemId,
                                              @RequestBody @Valid UpdateItemRequestDto itemDto) {
        return ResponseEntity.ok(itemService.update(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable Long itemId,
                                               @RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(itemService.getById(itemId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllItemsByOwner(@RequestHeader(USER_ID_HEADER) Long userId) {
        return ResponseEntity.ok(itemService.getAllByOwner(userId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> searchItems(@RequestParam String text) {
        return ResponseEntity.ok(itemService.search(text));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody @Valid CommentDto commentDto) {
        return ResponseEntity.ok(itemService.addComment(userId, itemId, commentDto));
    }
}
